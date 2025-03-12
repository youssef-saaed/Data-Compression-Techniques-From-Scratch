package huffman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.Hashtable;

public class Decompressor 
{
    BitSet compressedBits; 
    StringBuilder text; 
    Dictionary<String, Character> decodeTable;

    public Decompressor(String filename)
    {
        try   
        {
            byte[] bytes = Files.readAllBytes(Path.of(filename)); 
            compressedBits = BitSet.valueOf(bytes); 
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }

    private String getCompressedBits(Integer from, Integer to)
    {
        StringBuilder binaryString = new StringBuilder();
        
        // Loop within the given range in the bit set and extract binary string in this range 
        for (Integer i = from; i <= to; ++i)
        {
            binaryString.append(compressedBits.get(i) ? '1' : '0');
        }
        return binaryString.toString();
    }

    private Integer constructDecodeTable()
    {
        // Read the first 32 bits to get the header size (number of encoded symbols)
        String headerSizeBinaryString = getCompressedBits(0, 31);
        Integer headerSize = Integer.parseInt(headerSizeBinaryString, 2);
        decodeTable = new Hashtable<String, Character>();

        Integer current = 32; // Start reading after the header size bits
        while (headerSize > 0)
        {
            // Read the code length (8 bits)
            Integer codeLength = Integer.parseInt(getCompressedBits(current, current + 7), 2);
            current += 8;

            // Read the ASCII character (8 bits)
            Character symbol = (char)Integer.parseInt(getCompressedBits(current, current + 7), 2);
            current += 8;

            // Read the Huffman code (codeLength bits)
            String code = getCompressedBits(current, current + codeLength - 1);
            current += codeLength;

            decodeTable.put(code, symbol); // Map the huffman code to symbols
            --headerSize; 
        }       
        return current; // Return the end position of the header
    }

    public void decompress()
    {
        text = new StringBuilder(); 
        Integer start = constructDecodeTable(), end = start;
        StringBuilder currentCode = new StringBuilder();
        while (end < compressedBits.size())
        {
            currentCode.append(compressedBits.get(end) ? '1' : '0'); // Read the next bit and append to the current code
            ++end;
            Character codeChar = decodeTable.get(currentCode.toString());
            if (codeChar != null)
            {
                if (codeChar == '\0') break; // Check if there is a terminator (end of text)
                text.append(codeChar);
                start = end;
                currentCode = new StringBuilder();
            }
        }
    }

    public void save(String location)
    {
        try
        {
            Files.writeString(Path.of(location), text);
            System.out.println("Decompressed file saved at '%s'".formatted(location));
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }
}
