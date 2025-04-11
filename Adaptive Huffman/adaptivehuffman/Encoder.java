package adaptivehuffman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Encoder 
{
    private StringBuilder binaryString;
    private HuffmanTree tree;
    
    public Encoder()
    {
        clear();
    }

    public void clear()
    {
        binaryString = new StringBuilder();
        tree = new HuffmanTree();
    }

    private String toBinary(Character c)
    {
        Integer ascii = (int)c;
        String binaryAscii = "";
        for (Integer i = 0; i < 8; ++i)
        {
            binaryAscii = (ascii % 2 == 1 ? '1' : '0') + binaryAscii;
            ascii /= 2;
        }
        return binaryAscii;
    }

    public void appendChar(Character c)
    {
        c = (char)((int)c & 0x7f); // Masking the last 7 bits (ascii code) to avoid non ascii values 
        if (tree.exist(c)) // Non-first Occurrence
        {
            binaryString.append(tree.getCode(c));
        }
        else // First occurrence
        {
            binaryString.append(tree.getNYTCode());
            binaryString.append(toBinary(c));
        }
        tree.add(c);
    }

    public void appendString(String str)
    {
        for (Integer i = 0; i < str.length(); ++i)
        {
            appendChar(str.charAt(i));
        }
    }

    public void appendStringFromTextFile(String filePath)
    {
        String text = "";
        try
        {
            text = Files.readString(Path.of(filePath));
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
        appendString(text);
    }

    public String getEncodedBinaryString()
    {
        return binaryString.toString();
    }

    private byte[] binaryStringToByteArray()
    {
        byte[] bytes = new byte[(int)Math.ceil((double)binaryString.length() / 8)];
        for (int i = 0; i < binaryString.length(); i += 8)
        {
            String byteString = binaryString.substring(i, Math.min(i + 8, binaryString.length()));
            while (byteString.length() < 8) 
            {
                byteString += '0';   
            }
            bytes[i / 8] = (byte)Integer.parseInt(byteString, 2);             
        }
        return bytes;
    }

    public void writeToBinaryFile(String filePath)
    {
        byte[] bytes = binaryStringToByteArray();
        try
        {
            Files.write(Path.of(filePath), bytes);
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }
}
