package testing;

import adaptivehuffman.Decoder;
import adaptivehuffman.Encoder;

import org.junit.Assert;
import org.junit.Test;

public class AdaptiveHuffmanTest {

    @Test
    public void encoderTest()
    {
        Encoder encoder = new Encoder();

        encoder.appendString("ABCCCAAAA");
        String res = encoder.getEncodedBinaryString();
        Assert.assertEquals("010000010010000100001000011101000101110", res);
        encoder.clear();

        encoder.appendString("ABABABABAAA");
        res = encoder.getEncodedBinaryString();
        Assert.assertEquals("01000001001000010101101101111", res);
    }

    @Test
    public void decodeTest()
    {
        Decoder decoder = new Decoder();
        
        decoder.decodeBinaryString("0100000100100001000010000111010001011100");
        String res = decoder.getDecodedText();
        Assert.assertEquals("ABCCCAAAA", res);

        decoder.decodeBinaryString("01000001001000010101101101111000");
        res = decoder.getDecodedText();
        Assert.assertEquals("ABABABABAAA", res);
    }
}
