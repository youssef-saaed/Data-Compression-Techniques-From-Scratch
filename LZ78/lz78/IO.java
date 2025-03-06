package lz78;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class IO {
    private static Integer getKeyMask(ArrayList<Tag> tags)
    {
        Integer max = 0;
        for (Tag tag : tags)
        {
            max = Math.max(max, tag.getKey());
        }
        return Integer.highestOneBit(max);
    }   
    
    private static String binarize(int num, long maxMask)
    {
        String binaryNum = "";
        long mask = 1;
        while (mask <= maxMask)
        {
            if ((num & mask) > 0)
            {
                binaryNum = "1" + binaryNum;
            }
            else
            {
                binaryNum = "0" + binaryNum;
            }
            mask <<= 1;
        }
        return binaryNum;
    }

    private static byte[] binaryStringToByteArray(String binaryString)
    {
        while (binaryString.length() % 8 != 0)
        {
            binaryString += "0";
        }
        
        Integer n = binaryString.length() / 8;
        byte[] byteArray = new byte[n];
        for (Integer i = 0; i < n; ++i)
        {
            String binaryByte = binaryString.substring(i * 8, i * 8 + 8);
            byteArray[i] = (byte)Integer.parseInt(binaryByte, 2);
        }
        return byteArray;
    }

    private static String byteArrayToBinaryString(byte[] byteArray)
    {
        StringBuilder binaryString = new StringBuilder();
        Integer byteMask = 1 << 7;
        for (byte b : byteArray)
        {
            binaryString.append(binarize(b, byteMask));
        }
        return binaryString.toString();
    }

    public static void writeTagsBinary(String location, ArrayList<Tag> tags)
    {
        Integer maskKey = getKeyMask(tags), maskChar = 1 << 7;
        Long maskInt = (long)1 << 31;

        Integer nTags = tags.size(), keyBits = (int)(Math.log(maskKey) / Math.log(2) + 1);
        if (maskKey == 0)
        {
            keyBits = 1;
        }
        
        StringBuilder binaryString = new StringBuilder();
        binaryString.append(binarize(nTags, maskInt));
        binaryString.append(binarize(keyBits, maskInt));
        for (Tag tag : tags)
        {
            binaryString.append(binarize(tag.getKey(), maskKey));
            binaryString.append(binarize(tag.getNext(), maskChar));
        }

        byte[] byteArray = binaryStringToByteArray(binaryString.toString());

        try
        {
            Files.write(Path.of(location), byteArray);
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }

    public static ArrayList<Tag> readBinaryTags(String location)
    {
        try
        {
            byte[] byteArray = Files.readAllBytes(Path.of(location));
            String binaryString = byteArrayToBinaryString(byteArray);

            Integer nTags = Integer.parseInt(binaryString.substring(0, 32), 2);
            Integer keyBits = Integer.parseInt(binaryString.substring(32, 64), 2);
            Integer tagSize = keyBits + 8;

            ArrayList<Tag> tags = new ArrayList<Tag>(nTags);
            for (Integer i = 64; i + tagSize <= binaryString.length(); i += tagSize)
            {
                Integer key = Integer.parseInt(binaryString.substring(i, i + keyBits), 2);
                char next = (char)Integer.parseInt(binaryString.substring(i + keyBits, i + tagSize), 2);
                tags.add(new Tag(key, next));
            }
            return tags;
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
        return new ArrayList<Tag>();
    }
}
