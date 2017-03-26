import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, udf}

/**
  * Created by sneha on 3/24/17.
  */
object InvalidCountStats {

  /*
  UDF that changes string to -99999999 if integer
  and -999999999.999999999 if double
  */


  val func = udf((s1: String) => if (s1.forall(Character.isDigit)) {
    s1.toDouble
  } else -999999999.999999999)
  val func1 = udf((s1: String) => if (s1.forall(Character.isDigit)) {
    s1.toInt
  } else -99999999)

  def convertdatatype(colName: String, df: DataFrame, dtype: String): DataFrame = {

    dtype match {
      case "int" => var df1 = df.withColumn(s"$colName", func1(col(s"$colName")))
        return df1

      case "double" => var df1 = df.withColumn(s"$colName", func(col(s"$colName")))
        return df1
    }

  }

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("Solution-1")


    val spark = SparkSession.builder().config(conf).getOrCreate()
    var df = spark.read.format("com.databricks.spark.csv").option("escape", "\"").option("inferSchema", "true").option("treatEmptyAsNulls", "true").option("header", "true").load("/mnt/hgfs/Shared/data/test1.csv")


    df.printSchema()

    var df2 = convertdatatype("age", df, "int")

    df2.printSchema()

  }
}
