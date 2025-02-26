package lz77;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Decompressor {
    private ArrayList<ArrayList<Integer>> tags;
    private String text;

    public Decompressor(ArrayList<ArrayList<Integer>> tags)
    {
        this.tags = new ArrayList<ArrayList<Integer>>(tags);
    }

    public Decompressor(String filePath)
    {
        this(IO.readTagsInBinary(filePath)); // Calling the tags constructor with fetched tags from binary file
    }

    public void decompress()
    {
        this.text = "";
        Integer i = 0;
        for (ArrayList<Integer> tag : tags) // tag[0] -> back shift, tag[1] -> substring length, tag[2] -> next character
        {
            if (tag.get(0) != 0)
            {
                Integer ptr = i - tag.get(0), count = 0;
                while (count < tag.get(1))
                {
                    text += text.charAt(ptr);
                    ++ptr;
                    ++count;
                    ++i;
                }
            }
            if (tag.get(2) != 0)
            {
                text += (char)(int)tag.get(2);
                ++i;
            }
        }
    }

    public String getText()
    {
        return this.text;
    }
    
    public void save(String filePath)
    {
        try 
        {
            Files.write(Path.of(filePath), this.text.getBytes());
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }
}
