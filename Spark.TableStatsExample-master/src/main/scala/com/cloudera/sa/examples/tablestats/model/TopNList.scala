package com.cloudera.sa.examples.tablestats.model
import scala.util.control.Breaks._
import scala.collection.mutable
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.paranamer._
import scala.collection.mutable.ArrayBuffer
/**
 * Created by Sneha@FICO , December 2016 .
 */
class TopNList()  extends Serializable {
  val maxSize:Int = 100
  val topNCountsForColumnArray = new mutable.ArrayBuffer[(Any, Long)]
  var lowestColumnCountIndex:Int = -1
  var lowestValue = Long.MaxValue
  

  def add(newValue:Any, newCount:Long): Unit = {
    if (topNCountsForColumnArray.length < maxSize -1) {
      topNCountsForColumnArray += ((newValue, newCount))
    } else if (topNCountsForColumnArray.length == 25) {
      updateLowestValue
    } else {
      if (newCount > lowestValue) {
        topNCountsForColumnArray.insert(lowestColumnCountIndex, (newValue, newCount))
        updateLowestValue
      }
    }
  }

   
 def updateLowestValue: Unit = {
    var index = 0

    topNCountsForColumnArray.foreach{ r =>
      if (r._2 < lowestValue) {
        lowestValue = r._2
        lowestColumnCountIndex = index
      }
      index+=1
  }
 }
   override def toString() =  s"${topNCountsForColumnArray}"

}
