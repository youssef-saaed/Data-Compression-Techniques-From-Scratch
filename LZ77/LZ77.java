import lz77.*;

public class LZ77 {
    public static String helpString = "Compression usage: java LZ77.java compress [original file name] [compressed file name]\nDecompression usage: java LZ77.java decompress [compressed file name] [decompressed file name]";
    public static String errMessage = "Invalid Call For Help Type: java LZ77.java help";

    public static void main(String[] args)
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
                    if (args.length != 3) System.err.println(errMessage);
                    else 
                    {
                        Compressor compressor = new Compressor(args[1], true);
                        compressor.compress();
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
                        decompressor.decompress();
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
}
