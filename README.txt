The purpose of this work is to better identify and quantify the delays of the carriers within the USA.
The data consists of flight arrival and departure details for all commercial flights within the USA, from October 1987 to April 2008. There are nearly 120 million records in total, and takes up 1.6 gigabytes of space compressed and 12 gigabytes when uncompressed.

In order to package and run the Spark application locally, first you need to download all the individual years from http://stat-computing.org/dataexpo/2009/the-data.html and uncompress the files in the "input" folder.

Then, run the following command in terminal:

$ sudo sbt package && spark-submit --class "DelayCalculator" --master local[*] target/scala-2.10/delaycalculator_2.10-1.0.jar input output

