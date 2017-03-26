import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.stat._
/**
  * Created by sneha on 3/20/17.
  */
object medianComputation {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("Solution-1")


    val spark = SparkSession.builder().config(conf).getOrCreate()
    var df = spark.read.format("com.databricks.spark.csv").option("escape", "\"").option("inferSchema", "true").option("treatEmptyAsNulls", "true").option("header", "true").load("/mnt/hgfs/Shared/data/median.csv")

    df.createOrReplaceTempView("df")
    df.dtypes.map(x=> {
      if(x._2 == "IntegerType") {
        println("Column Name"+x._1)
        var colName=x._1
        df.select(s"$colName").limit(2).show()
        var op=  df.stat.approxQuantile(s"$colName",Array(0.5),0)
        op.foreach(x=>println(x))
        println("Output is")
      }
    })
  }
}