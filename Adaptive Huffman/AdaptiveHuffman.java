import adaptivehuffman.Encoder;
import adaptivehuffman.Decoder;

public class AdaptiveHuffman
{
    public static String helpString = "Encoding usage: java AdaptiveHuffman.java encode [original file name] [encoded file name]\nDecoding usage: java AdaptiveHuffman.java decode [encoded file name] [decoded file name]";
    public static String errMessage = "Invalid Call For Help Type: java AdaptiveHuffman.java help";
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
                case "encode":
                {
                    if (args.length != 3) System.err.println(errMessage);
                    else 
                    {
                        Encoder encoder = new Encoder();
                        encoder.appendStringFromTextFile(args[1]);
                        encoder.writeToBinaryFile(args[2]);
                    }
                    break;
                }
                case "decode":
                {
                    if (args.length != 3) System.err.println(errMessage);
                    else
                    {
                        Decoder decoder = new Decoder();
                        decoder.decodeTextFile(args[1]);
                        decoder.writeToTextFile(args[2]);
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