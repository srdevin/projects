/**
  * Created by sneha on 3/22/17.
  */


package com.coding.wrangling

import java.io._
import java.nio.charset._
import java.nio.{ByteBuffer, CharBuffer}
import java.util
import java.util.Base64

import sun.nio.cs.StandardCharsets
import java.nio.file.{Paths, Files}
import scala.io.Source
import java.nio.ByteBuffer

import org.slf4j.LoggerFactory
/**
  * Created by snehahegde on 11/3/17.
  */
object invalidUTF {
  val logger = LoggerFactory.getLogger(this.getClass)


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
      case ex :CharacterCodingException => logger.info("Character encoding exception")
        return  false
    }

    try {
      decoder.decode(tmp)
      return true;
    }
    catch {
      case ex: CharacterCodingException => logger.info("Character encoding exception")//println("Invalid abc"+decoder.decode(tmp))
        return  false
    }

  }

  def eachLineByArray(lineNo:Int,Row:Array[String],colCount:Int,output:String): Unit = {

    //val writer = new PrintWriter(new File(s"/home/sneha/Write.txt"))

    val w = new BufferedWriter(new FileWriter("/home/sneha/output.txt",true))

    Row.map(x=> {
      //println(x)

      if(!isUTF8MisInterpreted(x)) {
        // println("Invalid characters"+x)
        println("\n"+x + "\n Line no : "+lineNo)
        w.write("Invalid character is:"+x+": line no is :"+lineNo+"\n")
      }
    })
    //    Row.map(x=>println(x + "- " +x.getClass))


w.close()
  }

  def readCSV(inputPath:String,outputPath:String): Unit = {
    val csvFile = s"$inputPath"
    // var br:BufferedReader = null /media/sf_VM_Shared/invalid.csv

    if(Files.exists(Paths.get(s"$outputPath"))) {
      println("File Already exists")
      Files.delete(Paths.get(s"$outputPath"))
    }
    val cvsSplitBy = ","
    var br = new BufferedReader(new FileReader(csvFile))
    var line = ""
    var cnt = 1
    // use comma as separator
    line = br.readLine()

    var colCount = line.split(",").length
    while ((line ) != null) {

      var data: Array[String] = line.split(cvsSplitBy)
      // println("++++++++++I am in line +++++++++++++++++++", cnt)
      //println(data.foreach(println))
      eachLineByArray(cnt, data ,colCount,outputPath)
      line = br.readLine()
      cnt += 1
    }

  }


  def main(args: Array[String]): Unit = {

    readCSV("/mnt/hgfs/Shared/data/Invalidtest.csv","/home/sneha/output.txt")
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