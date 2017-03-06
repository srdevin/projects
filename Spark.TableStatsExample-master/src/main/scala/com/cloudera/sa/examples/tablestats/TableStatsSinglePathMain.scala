package com.cloudera.sa.examples.tablestats

import com.cloudera.sa.examples.tablestats.model.{ColumnStats, FirstPassStatsModel}
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
/**
 * Created by Sneha@FICO, Dec 2016
 */

object TableStatsSinglePathMain {
  
    val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
  @JsonIgnoreProperties(Array("list1"))
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
  
  def main(args: Array[String]): Unit = {

          
   
    val sparkConfig = new SparkConf()
    sparkConfig.set("spark.broadcast.compress", "false")
    sparkConfig.set("spark.shuffle.compress", "false")
    sparkConfig.set("spark.shuffle.spill.compress", "false")
    var sc = new SparkContext("local", "test", sparkConfig)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    val df = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").option("escape","\"").option("inferSchema", "true").option("treatEmptyValuesAsNulls","true").load("/media/sf_VM_Shared/loans1.csv")
       val firstPassStats = getFirstPassStat( df)
       val outJson = toJson(firstPassStats)
             
        println(outJson)
   


//    println("calculating the median value ")
//    import scala.collection.mutable.ArrayBuffer
//    var medianvalues = mutable.ArrayBuffer[Double]()
//
//    medianvalues += 1.0
//    medianvalues += 2.0
//    medianvalues += 3.0
//    medianvalues += 4.0
//    medianvalues += 5.0
//
//    val cnmstats = new ColumnStats()
//    val resultsmedian = cnmstats.calculateMedian(medianvalues);
//
//    println("resultsmedian :" +resultsmedian)
//
  }


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
      val firstPassStatsModel = new FirstPassStatsModel()
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
}
