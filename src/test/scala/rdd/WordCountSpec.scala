package rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.scalatest._

class WordCountExampleSpec extends FunSuite with Matchers with BeforeAndAfterAll {

  val fileLines = Array("Line One", "Line Two", "Line Three", "Line Four")

  val sparkConf = new SparkConf()
    .setAppName("WordCountSpec")
    .setMaster("local[2]")

  val sparkContext = new SparkContext(sparkConf)

  override def afterAll() {
    sparkContext.stop()
  }

  test("countWordsInFile should count words") {
    val inputRDD: RDD[String] = sparkContext.parallelize[String](fileLines)
    val results = WordCountExample.countWordsInFile(inputRDD).collect
    results should contain ("Line", 4)
  }

  test("splitFile should split the file into words"){
    val inputRDD: RDD[String] = sparkContext.parallelize[String](fileLines)
    val wordsRDD = WordCountExample.splitFile(inputRDD)
    wordsRDD.count() should be (8)
  }

  test("countWords should count the occurrences of each word"){
    val words = Array("word", "count", "example", "word")
    val inputRDD: RDD[String] = sparkContext.parallelize[String](words)
    val wordCounts = WordCountExample.countWords(inputRDD).collect
    wordCounts should contain ("word", 2)
    wordCounts should contain ("count", 1)
    wordCounts should contain ("example", 1)
  }

}
