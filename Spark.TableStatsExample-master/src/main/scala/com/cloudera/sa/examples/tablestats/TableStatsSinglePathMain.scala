package com.cloudera.sa.examples.tablestats

import com.cloudera.sa.examples.tablestats.model.{ColumnStats, FirstPassStatsModel }
import com.cloudera.sa.examples.tablestats.model.stdMedian
import org.apache.spark._
import org.apache.spark.sql.DataFrame

import scala.collection.mutable
import org.apache.spark.sql.catalyst.encoders.RowEncoder

import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.IndexedSeq
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.SparkSession
import org.json4s.native.Serialization
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature, annotation}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.util.parsing.json._
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Reads
import net.liftweb._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import  com.cloudera.sa.examples.tablestats.model
import com.google.gson.Gson
/**
 * Created by Sneha@FICO, Dec 2016
 */

object TableStatsSinglePathMain {
  
    val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)










  def toJson(value: Map[Symbol, Any]): String = {
    toJson(value map { case (k,v) => k.name -> v})
  }

  def toJson(value: Any): String = {
    mapper.writeValueAsString(value)
  }




  def toMap[V](json:String)(implicit m: Manifest[V]) = fromJson[Map[String,V]](json)

  def fromJson[T](json: String)(implicit m : Manifest[T]): T = {
    mapper.readValue[T](json)
  }

  val sparkConfig = new SparkConf()
  sparkConfig.set("spark.broadcast.compress", "false")
  sparkConfig.set("spark.shuffle.compress", "false")
  sparkConfig.set("spark.shuffle.spill.compress", "false")
  var sc = new SparkContext("local", "test", sparkConfig)
  val sqlContext = new org.apache.spark.sql.SQLContext(sc)

  val df = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").option("escape","\"").option("inferSchema", "true").option("treatEmptyValuesAsNulls","true").load("/mnt/hgfs/Shared/def.csv")



  val gson = new Gson()

  def main(args: Array[String]): Unit = {

          

 val firstPassStats = getFirstPassStat( df)
       //val outJson = toJson(firstPassStats)
         val outjsonInvalid = toJson(firstPassStats)
        println(outjsonInvalid)

    val columnmediansd = calculateMedian(df,df.columns)
    println("nodessss"+nodes)
    val mediansd = toJson("Hope to print nodes"+nodes)

    println("JSONNN"+Json.toJson(nodes))
  }

/*
def getStdMedian(df:DataFrame) : stdMedian = {
  val op = new stdMedian(df)
   println(op.getClass)
  return op
}*/


   def getFirstPassStat(df: DataFrame): FirstPassStatsModel = {
    val schema = df.schema
    

    val encoder = RowEncoder(schema)
    //Part B.1
   val columnValueCounts = df.rdd.flatMap(row =>
      (0 until schema.length).map { idx =>
        ((schema(idx).name, row.get(idx)), 1l)
         }
    ).reduceByKey(_+_).sortBy(_._2)
    

    //Part C
    val firstPassStats = columnValueCounts.mapPartitions[FirstPassStatsModel]{it =>
      val firstPassStatsModel = new FirstPassStatsModel
      it.foreach{ case ((columnIdx, columnVal), count) =>
        firstPassStatsModel += (columnIdx, columnVal, count)
      }
      Iterator(firstPassStatsModel)
    }.reduce { (a, b) => //Part D
      a += (b)
      a
    }

    firstPassStats

  }


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
}
