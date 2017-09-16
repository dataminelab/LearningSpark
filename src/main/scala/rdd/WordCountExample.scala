package rdd

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object WordCountExample {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("WordCountExample").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val file = sc.textFile("src/main/resources/data/wordcount.txt")
    val wordCounts = countWordsInFile(file)
    //wordCounts.saveAsTextFile("results.txt")

    println("*** WordCount results ***")
    wordCounts.foreach(println)
  }

  def countWordsInFile= splitFile _ andThen countWords _

  def splitFile(wordsByLine: RDD[String]): RDD[String] = {
    wordsByLine.flatMap(line => line.split(" "))
  }

  def countWords(words: RDD[String]): RDD[(String, Int)] = {
    words.map(word => (word, 1)).reduceByKey(_ + _)
  }

}