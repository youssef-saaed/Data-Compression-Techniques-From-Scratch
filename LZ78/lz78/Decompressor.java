package lz78;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Decompressor {
    private ArrayList<Tag> tags;
    private String text;

    public Decompressor(String path)
    {
        this(IO.readBinaryTags(path));
    }

    public Decompressor(ArrayList<Tag> tags)
    {
        this.tags = new ArrayList<Tag>(tags);
    }

    public void decompress()
    {
        text = "";
        Dictionary<Integer, String> decompressDictionary = new Hashtable<Integer, String>();
        decompressDictionary.put(0, "");
        Integer key = 1;

        for (Tag tag : tags)
        {
            String lastString = decompressDictionary.get(tag.getKey());
            if (tag.getNext() != 0)
            {
                lastString += tag.getNext();
            }
            text += lastString;
            decompressDictionary.put(key, lastString);
            ++key;
        }
    }

    public String getText()
    {
        return text;
    }

    public void save(String path)
    {
        try
        {
            Files.writeString(Path.of(path), text);
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }
}
