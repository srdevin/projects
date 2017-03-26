/**
  * Created by sneha on 3/18/17.
  */
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
object FicoWranglingObj {

  def main(args: Array[String]): Unit = {
    var dataMap = new scala.collection.mutable.HashMap[String,Double]
    def calmediansd(df:DataFrame):Unit = {

    }
    val conf = new SparkConf().setMaster("local").setAppName("Solution-1")


    val spark = SparkSession.builder().config(conf).getOrCreate()
    val data = spark.read.format("com.databricks.spark.csv").option("escape","\"").option("inferSchema", "true").option("treatEmptyAsNulls","true").option("header", "true").load("/mnt/hgfs/Shared/data/AutoLoans_ForReg.csv")
      data.printSchema()




    data.dtypes.map(x=> {
      if(x._2 == "StringType") {
        println("hey"+x._1)
        dataMap.put(x._1,0.0)
      }
      else if (x._2=="IntegerType"){
        var p = data.stat.approxQuantile(x._1,Array(0.5),0.0)
        dataMap.put(x._1,p(0))
       // dataMap.put(x._1,data.stat.approxQuantile(x._1,0.5,0.0))
      }
    })

  }

}
