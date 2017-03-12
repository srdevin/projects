package com.coding.wrangling

/**
  * Created by snehahegde on 11/3/17.
  */

import java.nio.CharBuffer
import java.nio.charset._
import java.nio.ByteBuffer
import java.nio.CharBuffer;

object stringInvalidUTF {
  /*def isUTF8MisInterpreted ( input:String, encoding:String) throws InterruptedException, CharacterCodingException {

    CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder()

    CharsetEncoder encoder = Charset.forName(encoding).newEncoder()

    val tmp = new ByteBuffer
    try
      val tmp1 = encoder.encode(CharBuffer.wrap(input));


    catch (CharacterCodingException e) {
      return false
    }

    try
      decoder.decode(tmp)
      return true;

    catch (CharacterCodingException e) {
      return false
    }
  }
*/

  def isUTF8MisInterpreted1 ( input:String, encoding:String){
    val decoder = Charset.forName("UTF-8").newDecoder
    decoder.onMalformedInput(CodingErrorAction.REPLACE)
    decoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    val bb = ByteBuffer.wrap(Array[Byte](0xD0.toByte, 0x9F.toByte, // 'П'
      0xD1.toByte, 0x80.toByte, // 'р'
      0xD0.toByte, // corrupted UTF-8, was 'и'
      0xD0.toByte, 0xB2.toByte, // 'в'
      0xD0.toByte, 0xB5.toByte, // 'е'
      0xD1.toByte, 0x82.toByte
    // 'т'
    ))
    val parsed = decoder.decode(bb)
    System.out.println(parsed)
  }


  def isUTF8MisInterpreted (){
    val decoder = Charset.forName("UTF-8").newDecoder
    decoder.onMalformedInput(CodingErrorAction.REPLACE)
    decoder.onUnmappableCharacter(CodingErrorAction.REPLACE)



    val str = "�"
    val abc = str.toByte
    // println("ABC"+abc)
    val bb = ByteBuffer.wrap(Array[Byte]
      (
        //        0xD0.toByte, c.toByte, // 'П'
        //      0xD1.toByte, 0x80.toByte, // 'р'
        //      0xD0.toByte, // corrupted UTF-8, was 'и'
        //      0xD0.toByte, 0xB2.toByte, // 'в'
        //      0xD0.toByte, 0xB5.toByte, // 'е'
        //      0xD1.toByte, 0x82.toByte ,
        str.toByte// 'т'
      ))



    val parsed = decoder.decode(bb)
    System.out.println(parsed)
  }

  def main(args:Array[String]): Unit = {
    println("Hello World")

isUTF8MisInterpreted()
  }

}
