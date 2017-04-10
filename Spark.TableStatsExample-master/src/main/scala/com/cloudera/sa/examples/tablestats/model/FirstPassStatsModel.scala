package com.cloudera.sa.examples.tablestats.model

import com.cloudera.sa.examples.tablestats.TableStatsSinglePathMain
import org.apache.spark.sql.DataFrame

import scala.collection.mutable


/**
 * Created by Sneha@FICO, Dec 2016
 * 
 */
class FirstPassStatsModel extends Serializable {
  var columnStatsMap = new mutable.HashMap[String,ColumnStats]


  def +=(colIndex: String, colValue: Any, colCount: Long ): Unit = {

    columnStatsMap.getOrElseUpdate(colIndex, new ColumnStats) += (colValue, colCount)

   // df.printSchema()

      }

  def +=(firstPassStatsModel: FirstPassStatsModel): Unit = {
    firstPassStatsModel.columnStatsMap.foreach { e =>
      val columnStats = columnStatsMap.getOrElse(e._1, null)
      if (columnStats != null ) {
        columnStats += (e._2)
      } else {
        columnStatsMap += ((e._1, e._2))
      }
    }
  }

  override def toString = s"$columnStatsMap"
}
