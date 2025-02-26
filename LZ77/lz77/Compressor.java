package lz77;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Compressor {
    private String text;
    private ArrayList<ArrayList<Integer>> tags;
    private Integer textSize;

    public Compressor(String textOrPath, boolean fromFile)
    {
        String text;
        if (fromFile)
        {
            try 
            {
                text = Files.readString(Path.of(textOrPath));
            }
            catch (IOException e)
            {
                System.err.println(e.getStackTrace());
                text = "";
            }
        }
        else 
        {
            text = textOrPath;
        }
        this.text = text;
        this.tags = new ArrayList<ArrayList<Integer>>();
        this.textSize = text.length();
    }
    
    private ArrayList<Integer> getBestWindow(Integer lookAheadWindow)
    {
        Integer windowSize = 1, bestIndex = -1, bestLength = -1;
        String searchString = this.text.substring(0, lookAheadWindow);
        // Loop while window is fitted in the search buffer and the lookahead window 
        while (windowSize <= lookAheadWindow && windowSize <= this.textSize - lookAheadWindow)
        {
            String subString = this.text.substring(lookAheadWindow, lookAheadWindow + windowSize);
            Integer index = searchString.lastIndexOf(subString);
            // Update the best window to be the largest window possible if there is a window found
            if (index != -1 && (bestIndex == -1 || (bestIndex != -1 && windowSize > bestLength)))
            {
                bestIndex = index;
                bestLength = windowSize;
            }
            ++windowSize;
        }
        return new ArrayList<Integer>(Arrays.asList(bestIndex, bestLength));
    }

    private ArrayList<Integer> getBestRepetitiveWindow(Integer lookAheadWindow)
    {
        Integer windowSize = 1, bestIndex = -1, bestLength = -1;
        // Loop while window is fitted in the search buffer and the lookahead window 
        while (windowSize <= lookAheadWindow && windowSize <= this.textSize - lookAheadWindow)
        {
            // Get the last characters with the window size from the search buffer
            String sequence = this.text.substring(lookAheadWindow - windowSize, lookAheadWindow);
            Integer length = 0;
            // Counting the repetition max length for a sequence in the lookahead window
            while (length < this.textSize - lookAheadWindow)
            {
                if (this.text.charAt(lookAheadWindow + length) != sequence.charAt(length % windowSize)) // length % windowSize map the repeated sequence index to pattern index
                {
                    break;
                }
                ++length;
            }
            // Take the highest length repetitive sequence available
            if (length > windowSize)
            {
                if (bestIndex == -1 || (bestIndex != -1 && windowSize + length > bestLength))
                {
                    bestIndex = windowSize;
                    bestLength = length;
                }
            }
            ++windowSize;
        }
        return new ArrayList<Integer>(Arrays.asList(bestIndex, bestLength));
    }

    private ArrayList<Integer> makeTag(ArrayList<Integer> window, Integer lookAheadWindow)
    {
        // Handle the no occurence of prefix string
        if (window.get(0) == -1) 
        {
            return new ArrayList<Integer>(Arrays.asList(0, 0, (int)this.text.charAt(lookAheadWindow)));
        }
        Integer positionShift = lookAheadWindow - window.get(0), size = window.get(1), next;
        if (lookAheadWindow + size == this.textSize) // Handle the termination tag
        {
            next = 0;
        }
        else
        {
            next = (int)this.text.charAt(lookAheadWindow + window.get(1));
        }
        return new ArrayList<Integer>(Arrays.asList(positionShift, size, next));
    }

    private ArrayList<Integer> makeReptitiveTag(ArrayList<Integer> window, Integer lookAheadWindow)
    {
        Integer positionShift = window.get(0), size = window.get(1), next;
        if (lookAheadWindow + size == this.textSize) // Handle the termination tag
        {
            next = 0;
        }
        else
        {
            next = (int)this.text.charAt(lookAheadWindow + size);
        } 
        return new ArrayList<Integer>(Arrays.asList(positionShift, size, next));
    }

    public void compress()
    {
        Integer lookAheadWindow = 0;
        while (lookAheadWindow < this.textSize)
        {
            ArrayList<Integer> bestWindow = getBestRepetitiveWindow(lookAheadWindow);
            if (bestWindow.get(0) != -1)
            {
                this.tags.add(makeReptitiveTag(bestWindow, lookAheadWindow));
                lookAheadWindow += bestWindow.get(1) + 1;
                continue;
            }
            bestWindow = getBestWindow(lookAheadWindow);
            this.tags.add(makeTag(bestWindow, lookAheadWindow));
            if (bestWindow.get(0) == -1)
            {
                ++lookAheadWindow;
            }
            else
            {
                lookAheadWindow += bestWindow.get(1) + 1;
            }
        }
    }
    
    public ArrayList<ArrayList<Integer>> getTags()
    {
        return new ArrayList<ArrayList<Integer>>(this.tags);
    }

    public void save(String filePath)
    {
        IO.writeTagsInBinary(tags, filePath);
    }
}
