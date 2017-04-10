package com.cloudera.sa.examples.tablestats.model
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.DoubleType

import scala.collection.mutable

/**
  * Created by sneha on 4/9/17.
  */
class stdMedian(df:DataFrame) {


  val hashMap = new mutable.HashMap[String,Double]()
  var nodes = Map.empty[String, List[Double]]

  def addNode(key: String, value: Double) =
    nodes += (key -> (value :: (nodes get key getOrElse Nil)))


  def compute_median(colName:String,arrayOfColumn:Array[Double]) : Double = {
    //var df1 = df.withColumn(s"$colName", df.col(s"$colName").cast(DoubleType))
    // var arr = df1.select(s"$colName").rdd.map(r=>r(0).asInstanceOf[Double]).collect().sortWith(_<_).toList


    var mid = arrayOfColumn.size/2
    if (arrayOfColumn.size % 2 == 1) {
      return arrayOfColumn(mid)
    } else {
      return (arrayOfColumn(mid-1) + arrayOfColumn(mid)) / 2.0
    }


  }


  def standardDeviation(arrayValues: Array[Double]) : Double ={
    val mean = arrayValues.sum/arrayValues.length
    val stdDev = Math.sqrt((arrayValues.map( _ - mean)
      .map(t => t*t).sum)/arrayValues.length)

    stdDev.toDouble
  }


  def calculateMedian(df:DataFrame,colName:Array[String]): Unit = {
    colName.map(x=> {
      df.select(x).dtypes.map(x=> {
        if(x._2 == "StringType") {
          // hashMap.put(x._1,0.0)
          addNode(x._1,0.0)
          addNode(x._1,0.0)
        }
        else {

          val colName = x._1

          var df1 = df.withColumn(s"$colName", df.col(s"$colName").cast(DoubleType))


          var listOfColumnValues = df1.select(s"$colName").rdd.map(r=>r(0).asInstanceOf[Double]).collect().sortWith(_<_)
          val medval = compute_median(x._1,listOfColumnValues)
          val stdDeviation = standardDeviation(listOfColumnValues)
          // hashMap.put(x._1,medval)
          addNode(x._1,medval)
          addNode(x._1,stdDeviation)
        }
      })
    } )
  }
  /*
    def main(args: Array[String]): Unit = {
      val conf = new SparkConf().setMaster("local").setAppName("CastVariables")
      val spark = SparkSession.builder().config(conf).getOrCreate()
      var df = spark.read.format("com.databricks.spark.csv").option("escape", "\"").option("inferSchema", "true").option("treatEmptyAsNulls", "true").option("header", "true").load("/mnt/hgfs/Shared/def.csv")

      calculateMedian(df,df.columns)

      // println("HashMap values"+hashMap)


      println("Values of nodes"+nodes)
    }
  */

println(nodes)
  //override def toString: String = s"output : ${nodes}"
}
