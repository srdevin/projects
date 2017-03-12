package com.coding.wrangling

import java.io.{BufferedReader, FileReader}
import java.nio.charset._
import java.nio.{ByteBuffer, CharBuffer}
import java.util
import java.util.Base64

import sun.nio.cs.StandardCharsets

import scala.io.Source
import java.nio.ByteBuffer

/**
  * Created by snehahegde on 11/3/17.
  */
object testInvalidUtf8 {
  def isUTF8MisInterpreted(input:String):Boolean = {
    //convenience overload for the most common UTF-8 misinterpretation
    //which is also the case in your question
    return isUTF8MisInterpreted(input, "Windows-1252")
  }

  def isUTF8MisInterpreted(input:String,encoding:String) :Boolean = {

    var decoder:CharsetDecoder= Charset.forName("UTF-8").newDecoder()
    var encoder:CharsetEncoder = Charset.forName(encoding).newEncoder()
    //ByteBuffer tmp:Array[Byte]=null
    var tmp:ByteBuffer=null
    try {
      tmp = encoder.encode(CharBuffer.wrap(input))
    }

      catch {
        case ex :CharacterCodingException => print("Invalid : ")
          return  false
      }

    try {
      decoder.decode(tmp)
      return true;
    }
    catch {
      case ex: CharacterCodingException => println("Invalid"+decoder.decode(tmp))
        return  false
    }

  }

  def eachLineByArray(lineNo:Int,Row:Array[String]): Unit = {

    Row.map(x=> {
      //println(x)

      if(!isUTF8MisInterpreted(x)) {
        //println("Invalid characters"+x)
        println(x)
      }
    })
//    Row.map(x=>println(x + "- " +x.getClass))



  }

  def readCSV(): Unit = {
    val csvFile = "/media/sf_VM_Shared/Invalidtest.csv"
    // var br:BufferedReader = null

    val cvsSplitBy = ","
    var br = new BufferedReader(new FileReader(csvFile))
    var line = ""
    var cnt = 1
      // use comma as separator
      line = br.readLine()

      while ((line ) != null) {
        if (cnt == 1) {

          line.drop(1)
        }
          var data: Array[String] = line.split(cvsSplitBy)
         // println("++++++++++I am in line +++++++++++++++++++", cnt)
          //println(data.foreach(println))
          eachLineByArray(cnt, data)
          line = br.readLine()
          cnt += 1
        }

  }


  def main(args: Array[String]): Unit = {

    readCSV()
/*
    val test = "hellö wörld"
   // isInvalidUTF(test)
    val test1 = "guide (but, yeah, itâ€™s okay to share it with â€˜em)."
    val test2 = "guide (but, yeah, it’s okay to share it with ‘em)."
*/

  // println( isUTF8MisInterpreted(test1))
   // println( isUTF8MisInterpreted(test2))

  }
}
