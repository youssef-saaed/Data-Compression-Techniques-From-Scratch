package adaptivehuffman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Decoder 
{
    private StringBuilder binaryString;
    private StringBuilder decodedText;
    private HuffmanTree tree;

    public void clear()
    {
        binaryString = new StringBuilder();
        decodedText = new StringBuilder();
        tree = new HuffmanTree();
    }
    
    public Decoder()
    {
        clear();
    } 

    private String byteToBinary(byte b)
    {
        Boolean firstBit = b < 0; // Check the first bit from signed byte
        b &= 0x7f; // Mask the last 7 bits from byte

        String binaryByte = "";
        for (Integer i = 0; i < 7; ++i)
        {
            binaryByte = (b % 2 == 1 ? '1' : '0') + binaryByte;
            b >>= 1;
        }

        if (firstBit)
        {
            binaryByte = '1' + binaryByte;
        }
        else
        {
            binaryByte = '0' + binaryByte;
        }
        return binaryByte;
    }

    private void readBinaryFile(String filePath)
    {
        clear();

        try
        {
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            for (byte b : bytes)
            {
                binaryString.append(byteToBinary(b));
            }
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }

    public void decode()
    {
        // Add the first symbol explicitly because there is no code for NYT
        decodedText.append((char)Integer.parseInt(binaryString.substring(0, 8), 2)); 
        binaryString.delete(0, 8);
        tree.add(decodedText.charAt(0));

        String nextCode = tree.getNextCode(binaryString.toString()); // Next huffman code from encoded string
        while (nextCode != "")
        {
            binaryString.delete(0, nextCode.length());
            
            Character nextChar;
            if (tree.getNYTCode() == nextCode) // NYT Huffman code
            {
                if (binaryString.length() < 8) 
                {
                    break;
                }
                nextChar = (char)Integer.parseInt(binaryString.substring(0, 8), 2);
                binaryString.delete(0, 8);
            }
            else // Symbol Huffman Code
            {
                nextChar = tree.getSymbol(nextCode);
            }

            tree.add(nextChar);
            decodedText.append(nextChar);

            nextCode = tree.getNextCode(binaryString.toString());
        }
    }

    public void decodeBinaryString(String binaryString)
    {
        clear();
        this.binaryString.append(binaryString);
        decode();
    }

    public void decodeTextFile(String filePath)
    {
        readBinaryFile(filePath);
        decode();        
    }

    public String getDecodedText()
    {
        return decodedText.toString();
    }

    public void writeToTextFile(String filePath)
    {
        try
        {
            Files.writeString(Path.of(filePath), decodedText.toString());
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }
}
