import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * Created by snehahegde on 10/3/17.
 */


public class testUTF {

public  static void main (String ... args) throws InterruptedException, CharacterCodingException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPLACE);
        decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        ByteBuffer bb = ByteBuffer.wrap(new byte[] {
                (byte) 0xD0, (byte) 0x9F, // 'П'
                (byte) 0xD1, (byte) 0x80, // 'р'
                (byte) 0xD0,              // corrupted UTF-8, was 'и'
                (byte) 0xD0, (byte) 0xB2, // 'в'
                (byte) 0xD0, (byte) 0xB5, // 'е'
                (byte) 0xD1, (byte) 0x82  // 'т'
        });
        CharBuffer parsed = decoder.decode(bb);
        System.out.println(parsed);

    }

    public static void main1(String... args) {

        String result = null;
        try {
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            FileInputStream  input = new FileInputStream(new File("/home/snehahegde/Desktop/scala/codingWrangling/src/main/resources/invalidText"));
            InputStreamReader reader = new InputStreamReader(input, decoder);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                sb.append(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            result = sb.toString();

            CharBuffer parsed = decoder.decode(ByteBuffer.wrap(result.getBytes()));
            System.out.println(parsed);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result);
    }


    public static void testUTFValidation(String[] args) throws InterruptedException, CharacterCodingException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPLACE);
        decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        ByteBuffer bb = ByteBuffer.wrap(new byte[] {
                (byte) 0xD0, (byte) 0x9F, // 'П'
                (byte) 0xD1, (byte) 0x80, // 'р'
                (byte) 0xD0,              // corrupted UTF-8, was 'и'
                (byte) 0xD0, (byte) 0xB2, // 'в'
                (byte) 0xD0, (byte) 0xB5, // 'е'
                (byte) 0xD1, (byte) 0x82  // 'т'
        });
        CharBuffer parsed = decoder.decode(bb);
        System.out.println(parsed);
        // this prints: Пр?вет
    }



}
