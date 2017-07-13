import org.apache.spark._
import scala.util.Try

object DelayCalculator {
  def main(args: Array[String]) {

    /*
    0-Year
    1-Month
    8-UniqueCarrier
    15-DepDelay
    16-Origin
    */

    if (args.length < 2) {
      println("You need <input-dir> <output-dir> parameters.")
      sys.exit(1)
    }

    val conf = new SparkConf().setAppName("DelayCalculator")
    val sc = new SparkContext(conf)
    val input = sc.textFile(args(0))

    val delay = input
                  .filter(line => {

                    if( line.substring(0,4) == "Year" ) {
                      false
                    }else{
                      Try(line.split("(;|,)")(15).toInt).isSuccess
                    }
                    
                  })
                  .map(line => {
                    // val line_data = line.split(",");
                    val line_data = line.split("(;|,)");

                    val dateCarrier = line_data(0) + "," +
                                      line_data(1) + "," +
                                      line_data(8) + "," +
                                      line_data(16);

                    val delay = ( (x:Int) => {
                                  if(x > 0) x
                                  else 0
                                } )( line_data(15).toInt );

                    (dateCarrier,delay)

                  })
                  .groupByKey()
                  .map(p => ( p._1 , p._2.toList ) )
                  .map(p => ( p._1 , ((x:List[Int]) => {

                      var totalFlights        = 0;
                      var totalDelayedFlights = 0;
                      var totalDelay          = 0;

                      var i = 0;
                      x.foreach(flightDelay => {
                        totalFlights = (totalFlights + 1);
                        totalDelay = (totalDelay + flightDelay);
                        if( flightDelay > 0 ) totalDelayedFlights = (totalDelayedFlights + 1);
                      } )

                      List(
                        totalFlights,
                        totalDelayedFlights,
                        totalDelay
                      ).mkString(",")

                    } )( p._2 )
                  ) )
                  .map( x => x._1 + "," + x._2 )
                  .coalesce(1,true)
                  .saveAsTextFile(args(1));

    }

}
