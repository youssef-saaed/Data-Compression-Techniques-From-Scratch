import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import adaptivehuffman.HuffmanTree;
import gui.GUIBroadcast;
import gui.MainFrame;

public class AdaptiveHuffmanGUI {
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Usage: java AdaptiveHuffmanGUI.java [text file name]");
            return;
        }
        try
        {
            String s = Files.readString(Path.of(args[0]));
            MainFrame frame = new MainFrame();
            GUIBroadcast broadcast = new GUIBroadcast(frame.getPanel());
            HuffmanTree tree = new HuffmanTree(broadcast);
            for (int i = 0; i < Math.min(s.length(), 20); ++i) 
            {
                tree.add(s.charAt(i));
            }
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
        
    }
}
