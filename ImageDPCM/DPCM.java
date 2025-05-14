import java.io.IOException;

import dpcm.Decoder;
import dpcm.Encoder;

import dpcm.predictor.Predictor.PredictorType;

public class DPCM {
    public static String helpString = "Encoding usage: java DPCM.java encode [original file name] [encoded file name] [quantization levels] [predictor]\nDecoding usage: java DPCM.java decode [encoded file name] [decoded file name]";
    public static String errMessage = "Invalid Call For Help Type: java DPCM.java help";

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
                    case "encode":
                    {
                        if (args.length != 5) System.err.println(errMessage);
                        else 
                        {
                            PredictorType type;
                            switch (args[4]) {
                                case "0":
                                    type = PredictorType.FIRST_ORDER;
                                    break;
                                case "1":
                                    type = PredictorType.SECOND_ORDER;
                                    break;
                                default:
                                    type = PredictorType.ADAPTIVE;
                                    break;
                            }
                            Encoder encoder = new Encoder(args[1], type, Integer.parseInt(args[3]));
                            encoder.encode();
                            encoder.save(args[2]);
                        }
                        break;
                    }
                    case "decode":
                    {
                        if (args.length != 3) System.err.println(errMessage);
                        else
                        {
                            Decoder decoder = new Decoder(args[1]);
                            decoder.decode();
                            decoder.save(args[2]);
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
