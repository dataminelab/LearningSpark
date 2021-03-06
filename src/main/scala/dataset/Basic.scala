package dataset

import org.apache.spark.sql.SparkSession

//
// Create Datasets of primitive type and tuple type ands show simple operations.
//
object Basic {

  def main(args: Array[String]) {
    val spark =
      SparkSession.builder()
        .appName("Dataset-Basic")
        .master("local[4]")
        .getOrCreate()

    // Encoders for most common types are automatically provided by importing spark.implicits._
    import spark.implicits._

    // Create a tiny Dataset of itnegers
    val s = Seq(10, 11, 12, 13, 14, 15)

    // For implicit explanation see:
    // https://stackoverflow.com/a/46252991/328989
    val ds = s.toDS()

    println("*** only one column, and it always has the same name")
    ds.columns.foreach(println(_))

    println("*** column types")
    ds.dtypes.foreach(println(_))

    println("*** schema as if it was a DataFrame")
    ds.printSchema()

    println("*** values > 12")
    // $ Converts $"col name" into a [[Column]].
    // Also see: https://spark.apache.org/docs/2.2.0/api/scala/index.html#org.apache.spark.sql.Column
    ds.where($"value" > 12).show()

    // This seems to be the best way to get a range that's actually a Seq and
    // thus easy to convert to a Dataset, rather than a Range, which isn't.
    val s2 = Seq.range(1, 100)

    println("*** size of the range")
    println(s2.size)

    val tuples = Seq((1, "one", "un"), (2, "two", "deux"), (3, "three", "trois"))
    val tupleDS = tuples.toDS()

    println("*** Tuple Dataset types")
    tupleDS.dtypes.foreach(println(_))

    // the tuple columns have unfriendly names, but you can use them to query
    println("*** filter by one column and fetch another")
    tupleDS.where($"_1" > 2).select($"_2", $"_3").show()

  }
}
