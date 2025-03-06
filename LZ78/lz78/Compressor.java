package lz78;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Compressor {
    private String text;
    private Integer textSize;
    private ArrayList<Tag> tags;

    public Compressor(String pathOrText, boolean fromFile)
    {
        if (fromFile)
        {
            try
            {
                text = Files.readString(Path.of(pathOrText));
            }
            catch (IOException e)
            {
                System.err.println(e.getStackTrace());
            }
        }
        else
        {
            text = pathOrText;
        }
        textSize = text.length();
        tags = new ArrayList<Tag>();
    }

    public void compress()
    {
        tags.clear();

        Dictionary<String, Integer> compressionDictionary = new Hashtable<String, Integer>();
        compressionDictionary.put("", 0);
        
        Integer pointer = 0, length = 0, key = 1;
        String current = "";
        while (pointer + length < textSize)
        {
            current += text.charAt(pointer + length);
            ++length;
            if (compressionDictionary.get(current) == null)
            {
                Integer lastKey = compressionDictionary.get(current.substring(0, length - 1));
                char next = current.charAt(length - 1);
                tags.add(new Tag(lastKey, next));

                compressionDictionary.put(current, key);
                ++key;

                pointer += length;
                length = 0;
                current = "";
            }
        }
        if (current != "")
        {
            Integer lastKey = compressionDictionary.get(current);
            char next = 0;
            tags.add(new Tag(lastKey, next));
        }
        System.err.println(tags);
    }

    public ArrayList<Tag> getTags()
    {
        return new ArrayList<Tag>(tags);
    }

    public void save(String path)
    {
        IO.writeTagsBinary(path, tags);
    }
}
