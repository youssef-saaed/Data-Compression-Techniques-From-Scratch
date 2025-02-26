package lz77;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class IO {
    // Calculate bit masks for the first two numbers in the tag based on max of each of them
    private static ArrayList<Integer> getBitMaxMask(ArrayList<ArrayList<Integer>> tags)
    {
        Integer maxP = 0, maxS = 0;
        for (ArrayList<Integer> tag : tags)
        {
            maxP = Math.max(maxP, tag.get(0));
            maxS = Math.max(maxS, tag.get(1));
        }
        maxP = Integer.highestOneBit(maxP); 
        maxS = Integer.highestOneBit(maxS);      
        return new ArrayList<Integer>(Arrays.asList(maxP, maxS));  
    }

    private static String integerToBinaryString(int i, long maxMask)
    {

        String str = "";
        long mask = 1;
        while (mask <= maxMask)
        {
            if ((mask & i) > 0)
            {
                str = "1" + str;
            }
            else
            {
                str = "0" + str;
            }
            mask <<= 1;
        }
        return str;
    }

    private static byte[] binaryStringToByteArray(String binaryString)
    {
        // Adding trailing zeros to binary strings that its length is not multiple of 8 
        while (binaryString.length() % 8 != 0)
        {
            binaryString += "0";
        }
        Integer n = (int)(binaryString.length() / 8);
        byte[] bytes = new byte[n]; 
        // Convert each substring with size 8 to byte
        for (Integer i = 0; i < n; ++i)
        {
            bytes[i] = (byte)Integer.parseInt(binaryString.substring(i * 8, i * 8 + 8), 2);
        }
        return bytes;
    }

    public static void writeTagsInBinary(ArrayList<ArrayList<Integer>> tags, String filePath)
    {
        ArrayList<Integer> bitMaxMask = getBitMaxMask(tags);
        String binaryString = "";
        binaryString += integerToBinaryString(tags.size(), (long)1 << 31);
        binaryString += integerToBinaryString((int)(Math.log(bitMaxMask.get(0)) / Math.log(2)) + 1, (long)1 << 31);
        binaryString += integerToBinaryString((int)(Math.log(bitMaxMask.get(1)) / Math.log(2)) + 1, (long)1 << 31);
        for (ArrayList<Integer> tag : tags)
        {
            binaryString += integerToBinaryString(tag.get(0), bitMaxMask.get(0));
            binaryString += integerToBinaryString(tag.get(1), bitMaxMask.get(1));
            binaryString += integerToBinaryString(tag.get(2), 128);
        }
        byte[] bytes = binaryStringToByteArray(binaryString);
        try 
        {
            Files.write(Path.of(filePath), bytes);
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }

    private static String byteToBinaryString(byte b)
    {
        String binaryString = "";
        int mask = 1;
        while (mask <= 128)
        {
            if ((mask & b) > 0)
            {
                binaryString = "1" + binaryString;
            }
            else
            {
                binaryString = "0" + binaryString;
            }
            mask <<= 1;
        }
        return binaryString;
    }

    public static ArrayList<ArrayList<Integer>> readTagsInBinary(String filePath) 
    {
        try 
        {
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            String binaryString = "";
            for (byte b : bytes)
            {
                binaryString += byteToBinaryString(b);
            }

            Integer tagCount, maxP, maxS;
            tagCount = Integer.parseInt(binaryString.substring(0, 32), 2);
            maxP = Integer.parseInt(binaryString.substring(32, 64), 2);
            maxS = Integer.parseInt(binaryString.substring(64, 96), 2);

            ArrayList<ArrayList<Integer>> tags = new ArrayList<ArrayList<Integer>>(tagCount);
            Integer tagSize = maxP + maxS + 8;
            for (Integer i = 96; i + tagSize < binaryString.length(); i += tagSize)
            {
                String tagBinary = binaryString.substring(i, i + maxP + maxS + 8);
                Integer position, length, next;
                position = Integer.parseInt(tagBinary.substring(0, maxP), 2);
                length = Integer.parseInt(tagBinary.substring(maxP, maxP + maxS), 2);
                next = Integer.parseInt(tagBinary.substring(maxP + maxS), 2);
                tags.add(new ArrayList<Integer>(Arrays.asList(position, length, next)));
            }
            return tags;
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
        return new ArrayList<>();
    }
}
