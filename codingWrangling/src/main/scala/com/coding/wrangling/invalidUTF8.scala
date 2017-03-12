package com.coding.wrangling
package com.coding.wrangling
import java.io._

import scala.io.{Codec, Source}
import java.nio.charset.{Charset, CharsetDecoder, CodingErrorAction}
/**
  * Created by snehahegde on 9/3/17.
  */
object invalidUTF8 {

  def toSource(inputStream:InputStream): scala.io.BufferedSource = {
    import java.nio.charset.Charset
    import java.nio.charset.CodingErrorAction
    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    scala.io.Source.fromInputStream(inputStream)(decoder)
  }


  def main(args:Array[String]){

    var result:String = null




    try {
    //val inp = new FileInputStream(new File("/media/sf_VM_Shared/loans1.csv"))
    val inp = new FileInputStream(new File("/home/snehahegde/Desktop/scala/codingWrangling/src/main/resources/invalidText"))

    //val src = Source.fromFile("/media/sf_VM_Shared/loans1.csv")("UTF-8").foreach(print)
    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    val reader = new InputStreamReader(inp, decoder)
    val  bufferedReader = new BufferedReader( reader )
    val sb = new StringBuilder()
    var line = bufferedReader.readLine()
    while( line != null ) {
      sb.append( line )
      line = bufferedReader.readLine()
    }
    bufferedReader.close()
    result = sb.toString()

  } catch  {
      case ex : FileNotFoundException => {
        println("Missinf file exception")
      }

      case ex:IOException => {
        println("IO exception")
      }
    }

  System.out.println("Print invalid characters"+result)



}

}
