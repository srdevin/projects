package com.cloudera.sa.examples.tablestats.model

import java.util

import org.slf4j.LoggerFactory
import java.util.Collections

import scala.collection.JavaConverters._
/**
  * Created by Sneha@FICO, Dec 2016
  */

class ColumnStats(var nullCnt: Long = 0l,
                  var validCount: Long = 0l,
                  var totalCount: Double = 0.0,
                  var uniqueValues: Long = 0l,
                  var maxLong: Double = Double.MinValue,
                  var minLong: Double = Double.MaxValue,
                  var sumLong: Double = 0.0,
                  var invalid: Long = 0,
                  var meanLong: Double = 0.0,
                  var stdDeviation: Double = 0.0,
                  var median: Double = 0 ,
                  var nullnew :Long =0  ,
                  val topNValues: TopNList = new TopNList()) extends Serializable {



  /* Function to check if value is a string*/
  def isString(s: String): Boolean = {

    if (s == null || s.length == 0) {
      //logger.error("String field is empty")
    }

    val pattern = "^[A-Za-z, ]++$"
    return (s.matches(pattern))

  }

  def isnullorEmpty(s:Any) :Boolean = {
    return  (s.toString.isEmpty || s.equals(null) || s.toString.length == 0)


  }


  def isInvalid(s: String): Boolean = {
    return (isInteger(s) || isDouble(s) || isString(s))
  }

  /* Function to check if given string is an integer */
  def isInteger(s: String): Boolean = {
    try {
      Integer.parseInt(s);
      return true;
    }
    catch {
      case e: Exception => println("waste")
        //logger.error("Given String is not an integer" + e.getMessage.toString())
        return false

    }
  }

  def isDouble(s: String): Boolean = {
    try {
      s.toDouble
      return true
    }
    catch {
      case e: Exception => return false
    }
  }


  def nullemp(s: String): Boolean = {
    return (s.isEmpty() || s == "")
  }


  def standardDeviation(xs:scala.collection.mutable.ArrayBuffer[Double], meanLong: Double): Double = xs match {



    case ys => math.sqrt((0.0 /: ys) {
      (a, e) => a + math.pow(e - meanLong, 2.0)
    } / xs.size)
    case _ => 0.0
  }


  private var list1 = scala.collection.mutable.ArrayBuffer[Double]()
  //private var list1 = new util.ArrayList[Double]()
  
  def +=(colValue: Any, colCount: Long): Unit = {
    //    var list1 = scala.collection.mutable.ArrayBuffer[Double]()
    try {


      totalCount += colCount
      uniqueValues += 1


      if (colValue.isInstanceOf[Long] || colValue.isInstanceOf[Int] || colValue.isInstanceOf[Double] || colValue.isInstanceOf[Float]) {
        val colLongValue = colValue.asInstanceOf[Number].doubleValue()

        if (maxLong < colLongValue) maxLong = colLongValue
        if (minLong > colLongValue) minLong = colLongValue
        sumLong += colLongValue

        if (colLongValue.isNaN()) {
          invalid += 1
        }
        if (colCount <= 1) {
           list1 += colLongValue
          //list1.add(colLongValue)
        }
        if (colCount > 1) {
          sumLong += (colLongValue * (colCount - 1))

          for (j <- 0 to colCount.toInt - 1) {

            list1 += colLongValue
            // list1.add(colLongValue)

          }
        }



      }

      else if (colValue.isInstanceOf[String]) {

        sumLong = 0.0
        maxLong = 0.0
        minLong = 0.0
        stdDeviation = 0.0


      }

      totalCount = totalCount.toLong


    }
    catch {
      case nfe: NumberFormatException => println(nfe.getMessage)
      case e: Exception => e.printStackTrace()

    }


    topNValues.add(colValue, colCount)
    topNValues.topNCountsForColumnArray.foreach { r =>
      if (r._1 == null) {
        nullCnt = r._2
      }
      else if (r._1 == "" | r._1.toString.trim().length == 0 || r._1.toString.isEmpty) {
        nullCnt = r._2
      }
    }


    validCount = totalCount.toLong - invalid - nullCnt
    var tmp = totalCount

    if(tmp == validCount) {
      meanLong = sumLong / tmp
    } else {
      meanLong = sumLong/validCount
    }
    stdDeviation = standardDeviation(list1, meanLong)
    // median = calculateMedian(list1)





  }

  //Part B.1.2
  def +=(ColumnStats: ColumnStats): Unit = {
    nullCnt += ColumnStats.nullCnt
    nullnew += ColumnStats.nullnew
    totalCount += ColumnStats.totalCount
    uniqueValues += ColumnStats.uniqueValues
    invalid += ColumnStats.invalid
    validCount += ColumnStats.validCount
    sumLong += ColumnStats.sumLong
    maxLong = maxLong.max(ColumnStats.maxLong)
    meanLong += ColumnStats.meanLong
    minLong = minLong.min(ColumnStats.minLong)
    stdDeviation+= ColumnStats.stdDeviation
    //median += ColumnStats.median
    ColumnStats.topNValues.topNCountsForColumnArray.foreach { r =>
      topNValues.add(r._1, r._2)

    }

  }


  override def toString = s"ColumnStats(nullnew=$nullnew,nullCnt=$nullCnt,validCount = $validCount, invalid_count = $invalid ,totalCount=$totalCount, uniqueValues=$uniqueValues, maxLong=$maxLong, minLong=$minLong, sumLong=$sumLong,meanLong=$meanLong,stdDeviation = $stdDeviation,median =$median ,topNValues=${topNValues.topNCountsForColumnArray})"

  // override def toString = s"ColumnStats(nulls=$nulls, empties=$empties, totalCount=$totalCount, uniqueValues=$uniqueValues, sumLong=$sumLong, topNValues=$topNValues, avgLong=$avgLong)"
}