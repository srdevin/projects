package com.cloudera.sa.examples.tablestats.model
/**
 * Created by Sneha@FICO, Dec 2016
 */

class ColumnStats(var nullCnt:Long =0l,
                  var validCount:Long = 0l,
                  var totalCount: Double = 0.0,
                  var uniqueValues:Long = 0l,
                  var maxLong:Double = Double.MinValue,
                  var minLong:Double = Double.MaxValue,
                  var sumLong:Double = 0.0,
                  var invalid:Long = 0,
                  var meanLong:Double = 0.0 ,
                  var stdDeviation:Double = 0.0 ,
                  var median:Double = 0.0 ,
                  var empcnt:Long = 0 ,
                  val topNValues:TopNList = new TopNList()) extends Serializable {

 def isString(s:String):Boolean = {
    val pattern = "^[A-Za-z, ]++$"
    return (s.matches(pattern))

  }
 
 


 def isInvalid(s:String):Boolean = {
     return (isInteger(s) || isDouble(s) || isString(s)) 
   }

  def  isInteger(s:String):Boolean = {
    try {
      Integer.parseInt(s);
      return true;
    }
    catch {
      case e: Exception =>   return false

    }
  }

  def isDouble(s:String):Boolean = {
    try {
      s.toDouble
      return true
    }
    catch {
      case e : Exception => return false
    }
  }
  
  
  def nullemp(s:String) :Boolean = {
   return (s.isEmpty()|| s == "" )
  }
  

  
  

  

 
   def +=(colValue: Any, colCount: Long): Unit = {
val list1 = scala.collection.mutable.ListBuffer[Double]()


  try {
  
  
  totalCount += colCount
  uniqueValues += 1  

  def standardDeviation(xs:scala.collection.mutable.ListBuffer[Double], meanLong:Double): Double = xs match {
    
  case ys => math.sqrt((0.0 /: ys) {
   (a,e) => a + math.pow(e - meanLong, 2.0)
  } / xs.size)
}
  
  
  def calculateMedian(l1:scala.collection.mutable.ListBuffer[Double]) :Double = {
    var med:Double =0.0
    //var lower:Double = 0.0
   // var upper:Double = 0.0
    
    if(l1 == null || l1.size == 0) 
      return 0.0 
    var (lower, upper) = l1.sortWith(_<_).splitAt(l1.size / 2)
    if (l1.size % 2 == 0) {
    med = (lower.last + upper.head) / 2.0
    }
    else {
      med = upper.head
    }
   
   return med
  }
 
   

  if (colValue.isInstanceOf[Long] || colValue.isInstanceOf[Int] || colValue.isInstanceOf[Double] || colValue.isInstanceOf[Float]) {
     val colLongValue = colValue.asInstanceOf[Number].doubleValue()
     list1 += colLongValue
    
      if (maxLong < colLongValue) maxLong = colLongValue
      if (minLong > colLongValue) minLong = colLongValue
      sumLong += colLongValue
    
      if(colLongValue.isNaN()) {
        invalid += 1
      }
     
      
     
     
    
  }
  
  else  if (colValue.isInstanceOf[String]) {
    
     sumLong = 0.0
      maxLong = 0.0
      minLong = 0.0
      stdDeviation = 0.0
 
    
  }



 if(colCount > 1 ) {
   if (colValue.isInstanceOf[Long] || colValue.isInstanceOf[Int] || colValue.isInstanceOf[Double] || colValue.isInstanceOf[Float]) {
     val colLongValue = colValue.asInstanceOf[Number].doubleValue()
     sumLong += (colLongValue * (colCount-1))
     for(j<-0 to colCount.toInt -2) {
  
       list1 += colLongValue
       
     }
   }
 }
 
 
 
 var temp:Double=0.0
 
 totalCount = totalCount.toLong
 meanLong = sumLong/totalCount
 
if (colValue.isInstanceOf[Long] || colValue.isInstanceOf[Int] || colValue.isInstanceOf[Double] || colValue.isInstanceOf[Float]) {
     val colLongValue = colValue.asInstanceOf[Number].doubleValue()
     list1 += colLongValue
  stdDeviation = standardDeviation(list1, meanLong)
  median = calculateMedian(list1)
   }

  }
catch {
  case nfe: NumberFormatException=> println(nfe.getMessage)
  case e: Exception => e.printStackTrace()

}


  
    topNValues.add(colValue, colCount)
 topNValues.topNCountsForColumnArray.foreach{ r =>
        if(r._1 == null) {
          nullCnt = r._2
        }
          else if (r._1 == "" | r._1.toString.trim().length == 0 || r._1.toString.isEmpty) {
          nullCnt = r._2
      }
      }
    
      empcnt = totalCount.toLong - nullCnt
      validCount = totalCount.toLong -invalid - nullCnt
    
   } 
  //Part B.1.2
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
    //stdDeviation+= ColumnStats.stdDeviation
    //median += ColumnStats.median
    empcnt += ColumnStats.empcnt
      ColumnStats.topNValues.topNCountsForColumnArray.foreach{ r =>
      topNValues.add(r._1, r._2)
      
    }
     
  }
  
  

  override def toString = s"ColumnStats(nullCnt=$nullCnt,validCount = $validCount, invalid_count = $invalid ,totalCount=$totalCount, uniqueValues=$uniqueValues, maxLong=$maxLong, minLong=$minLong, sumLong=$sumLong,meanLong=$meanLong,stdDeviation = $stdDeviation,median =$median ,topNValues=${topNValues})"
 // override def toString = s"ColumnStats(nulls=$nulls, empties=$empties, totalCount=$totalCount, uniqueValues=$uniqueValues, sumLong=$sumLong, topNValues=$topNValues, avgLong=$avgLong)"
}