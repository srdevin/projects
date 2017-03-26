import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
/**
  * Created by sneha on 3/22/17.
  */
object invalidDTChange {




 val func = udf( (s1:String) => if(s1.forall(Character.isDigit)) {s1.toDouble} else -999999999.999999999 )


  def convertdatatype(colName:String,df:DataFrame) :Unit ={
   var df1 = df.withColumn(s"$colName",func(col(s"$colName")))
    df1.select(s"$colName").show()
   // df = df.withColumn(s"$colName", when(s"$colName".toDouble,0).otherwise(1)
  }

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("Solution-1")


    val spark = SparkSession.builder().config(conf).getOrCreate()
    var df = spark.read.format("com.databricks.spark.csv").option("escape", "\"").option("inferSchema", "true").option("treatEmptyAsNulls", "true").option("header", "true").load("/mnt/hgfs/Shared/data/test1.csv")


    df.printSchema()

    convertdatatype("age",df)


  }
}
