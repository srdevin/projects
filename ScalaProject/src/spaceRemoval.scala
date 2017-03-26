import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{IntegerType, StringType}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{trim, length, when}
import org.apache.spark.sql.functions.regexp_replace
import org.apache.spark.sql.functions.ltrim
import org.apache.spark.sql.functions.rtrim
/**
  * Created by sneha on 3/19/17.
  */
object spaceRemoval {

  def main(args: Array[String]): Unit = {
    def calmediansd(df: DataFrame): Unit = {

    }

    val conf = new SparkConf().setMaster("local").setAppName("Solution-1")


    val spark = SparkSession.builder().config(conf).getOrCreate()
    var data = spark.read.format("com.databricks.spark.csv").option("escape", "\"").option("inferSchema", "true").option("treatEmptyAsNulls", "true").option("header", "true").load("/mnt/hgfs/Shared/data/test2.csv")



    def determinedt(colName: String): Boolean = {
      data.schema(s"$colName").dataType  match {
        case StringType => return true
        case IntegerType => return false
      }

    }


    def trimPrefixSuffixChars(string: String, invalidCharsFunction: (Char) => Boolean = (c) => c == ' '): String = {
      if (string.nonEmpty)
        string
          .dropWhile(char => invalidCharsFunction(char)) //trim prefix
          .reverse
          .dropWhile(char => invalidCharsFunction(char)) //trim suffix
          .reverse
      else
        string
    }

    var name = "   sneha      hegde            "
    println(trimPrefixSuffixChars(s"$name").length)
     //data.select("name").foreach(x=>println(x+"value"+"lenght"+x.toString().length))

   // var data1 = data.withColumn("name", regexp_replace(data.col("name")," ", ""))

    data.schema("name").dataType  match {
      case StringType => data = data.withColumn("name", regexp_replace(data.col("name")," ", ""))
    }

    data.select("name").show()
   /* println("LTRIM")
    var d1 = data.select(ltrim(data.col("name")))
    d1.foreach(x=> {
      println("colval"+x+"length"+x.length)
    })


    println("RTRIM")
    var d2 = data.select(rtrim(data.col("name")))
    d2.foreach(x=> {
      println("colval"+x+"length"+x.length)
    })


    println("TRIM")
    var d3 = data.select(trim(data.col("name")))
    d3.foreach(x=> {
      println("colval"+x+"length"+x.length)
    })

*/
   }

  }

