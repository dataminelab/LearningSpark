package dataset

import org.apache.spark.sql.SparkSession

object S3WordCount {
  def main(args: Array[String]): Unit = {

    val spark =
      SparkSession.builder()
        .appName("SQL-CaseClassSchemaProblem")
        .master("local[4]")
        .getOrCreate()

    import spark.implicits._

    val lines = spark.read.text("s3://support.elasticmapreduce/bigdatademo/sample/wiki").as[String]
    val words = lines.flatMap(value => value.split("\\s+")).filter(_ != "")
    val groupedWords = words.groupByKey(value => value)
    val wordCounts = groupedWords.count()

    // order by descending order and store in s3
    wordCounts.orderBy($"count(1)".desc).rdd.saveAsTextFile("s3://radek-training/wiki-wordcount")

  }
}
