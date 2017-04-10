package com.cloudera.sa.examples.tablestats.model

import org.apache.spark.sql.DataFrame

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
                  var median: Double = 0,
            // private  var mediancomp :medianComputation = new medianComputation(),
                  val topNValues: TopNList = new TopNList()) extends Serializable {
  /**
    * Returns standard deviation of elements in the collection
    *
    * @param xs
    * @param meanLong
    */

  def standardDeviation(xs: scala.collection.mutable.ArrayBuffer[Double], meanLong: Double): Double = xs match {

    case ys => math.sqrt((0.0 /: ys) {
      (a, e) => a + math.pow(e - meanLong, 2.0)
    } / xs.size)
    case _ => 0.0
  }



  private var list1 = scala.collection.mutable.ArrayBuffer[Double]()
  private var setdata = scala.collection.mutable.Set[String]()

  private var stackcount = scala.collection.mutable.Stack[String]()

  /**
    * Computes all the statistics required , considering colvalue and count of that value
    *
    * @param colCount
    * @param colValue
    */
  def +=(colValue: Any, colCount: Long ): Unit = {

    if (colValue == "" || colValue == null) {
      nullCnt += colCount
    }


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
        }
        if (colCount > 1) {
          sumLong += (colLongValue * (colCount - 1))

          for (j <- 0 to colCount.toInt - 1) {

            list1 += colLongValue
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

    validCount = totalCount.toLong - invalid - nullCnt
    var tmp = totalCount

    if (tmp == validCount) {
      meanLong = sumLong / tmp
    } else {
      meanLong = sumLong / validCount
    }


  }


  /**
    * Computes all the statistics required , Calculating the aggregation of all statistics for a particular column
    *
    * @param ColumnStats
    */
  def +=(ColumnStats: ColumnStats): Unit = {
    nullCnt += ColumnStats.nullCnt
    totalCount += ColumnStats.totalCount
    uniqueValues += ColumnStats.uniqueValues
    invalid += ColumnStats.invalid
    validCount += ColumnStats.validCount
    sumLong += ColumnStats.sumLong
    maxLong = maxLong.max(ColumnStats.maxLong)
    meanLong += ColumnStats.meanLong
    minLong = minLong.min(ColumnStats.minLong)
    stdDeviation += ColumnStats.stdDeviation
    median += ColumnStats.median
    ColumnStats.topNValues.topNCountsForColumnArray.foreach { r =>
      topNValues.add(r._1, r._2)

    }

  }




  override def toString = s"ColumnStats(nullCnt=$nullCnt,validCount = $validCount, invalid_count = $invalid ,totalCount=$totalCount, uniqueValues=$uniqueValues, maxLong=$maxLong, minLong=$minLong, sumLong=$sumLong,meanLong=$meanLong,stdDeviation = $stdDeviation,median =$median ,topNValues=${topNValues.topNCountsForColumnArray})"

}