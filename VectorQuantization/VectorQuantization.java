import java.io.IOException;

import vectorquantization.Compressor;
import vectorquantization.Decompressor;

public class VectorQuantization {
    public static String helpString = "Compression usage: java VectorQuantization.java compress [original file name] [compressed file name] [number of codebook] [block size]\nDecompression usage: java VectorQuantization.java decompress [compressed file name] [decompressed file name]";
    public static String errMessage = "Invalid Call For Help Type: java VectorQuantization.java help";

    public static void main(String[] args)
    {
        try
        {
            if (args.length == 0)
            {
                System.err.println(errMessage);
            }
            else 
            {
                String operation = args[0];
                switch (operation) {
                    case "help":
                    {
                        if (args.length != 1) System.err.println(errMessage);
                        else System.out.println(helpString);
                        break;
                    }
                    case "compress":
                    {
                        if (args.length != 5) System.err.println(errMessage);
                        else 
                        {
                            Compressor compressor = new Compressor(args[1], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                            compressor.save(args[2]);
                        }
                        break;
                    }
                    case "decompress":
                    {
                        if (args.length != 3) System.err.println(errMessage);
                        else
                        {
                            Decompressor decompressor = new Decompressor(args[1]);
                            decompressor.save(args[2]);
                        }
                        break;
                    }
                    default:
                    {
                        System.err.println(errMessage);
                        break;
                    }
                }
            } 
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }
}
