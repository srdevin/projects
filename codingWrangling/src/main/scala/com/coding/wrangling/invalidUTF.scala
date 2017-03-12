package com.coding.wrangling
import java.io.FileNotFoundException
import java.nio.charset.{CharsetDecoder, CodingErrorAction}

import scala.io.{Codec, Source}
import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction

import org.w3c.dom.css.CSSValue
/**
  * Created by snehahegde on 9/3/17.
  */
object invalidUTF {

  def main(args:Array[String]){
 println("Hello ")
    var res:String = null



    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.REPORT)
    val abc = Source.fromFile("/media/sf_VM_Shared/loans1.csv")(decoder).getLines().toList

    //println("Decoder tyle"+abc)

  /*  try {
    val bufferedSource = io.Source.fromFile("/media/sf_VM_Shared/loans1.csv")(decoder)

    val sb = new StringBuilder

    for(line <- bufferedSource.getLines()) {

      if(line != null) {
        sb.append(line)
      }

    }
    bufferedSource.close()

     res = sb.toString()
    }

    println("Result is "+res) */

  }


}
