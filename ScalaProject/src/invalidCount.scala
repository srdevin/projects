import java.io.File
import java.util.Calendar

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.{DataFrame, SparkSession}
import java.nio.file.{Files, Paths}
import org.apache.commons.io.FileUtils

/**
  * Created by sneha on 3/20/17.
  */
object invalidCount {


  def changeDT(colName:String,df:DataFrame):DataFrame = {
var tempdf:DataFrame=null
    tempdf= df.withColumn(s"$colName", df.col(s"$colName").cast(IntegerType))

    return tempdf
  }

  import org.apache.commons.io.FileUtils
  import org.apache.commons.io.filefilter.WildcardFileFilter

  import java.io._


  def saveAsCSV(locationToSave:String,df:DataFrame) {

    println("Deletion done")
    //FileUtils.deleteDirectory(new File(s"$locationToSave"))
    FileUtils.deleteDirectory(new File(s"$locationToSave"))

    //delete(new File(s"$locationToSave"))
    df.write.csv(s"$locationToSave")

    println("Saving done")
  }


  def saveAsZipCSV(locationToSave:String,df:DataFrame,format:String) {


    format match {
      case "gzip" => df.write.format("com.databricks.spark.csv").option("codec", "org.apache.hadoop.io.compress.GzipCodec").save(s"$locationToSave")
      case "bzip2" => df.write.format("com.databricks.spark.csv").option("codec", "org.apache.hadoop.io.compress.BZip2Codec").save(s"$locationToSave")
    }

  }

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("Solution-1")


    val spark = SparkSession.builder().config(conf).getOrCreate()
    var df = spark.read.format("com.databricks.spark.csv").option("escape", "\"").option("inferSchema", "true").option("treatEmptyAsNulls", "true").option("header", "true").load("/mnt/hgfs/Shared/data/test1.csv")


    df.printSchema()


   FileUtils.deleteDirectory(new File("/home/sneha/data"))

    saveAsCSV("/home/sneha/data",df)


  }
}