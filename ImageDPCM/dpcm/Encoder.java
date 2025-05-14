package dpcm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import dpcm.predictor.AdaptivePredictor;
import dpcm.predictor.FirstOrderPredictor;
import dpcm.predictor.Predictor;
import dpcm.predictor.SecondOrderPredictor;
import dpcm.predictor.Predictor.PredictorType;

public class Encoder 
{
    private Integer n;
    private Integer m;
    private Integer minDifference;
    private Integer maxDifference;
    private Integer bits;
    private Integer[][] image;
    private Integer[][] quantizedDifferences;
    private Integer levels;
    private Predictor predictor;
    private UniformQuantizer quantizer;
    private Decoder decoder;

    public Encoder(String imagePath, PredictorType type, Integer levels) throws IOException
    {
        
        Integer [][][] image = ImageProcessor.loadImage(imagePath);     
        n = image.length;
        m = image[0].length; 
        this.image = ImageProcessor.rgbToGray(image); 
        this.quantizedDifferences = new Integer[n - 1][m - 1];
        
        bits = (int)Math.ceil(Math.log(levels) / Math.log(2));
        this.predictor = getPredictor(type);
        
        calculateMinAndMaxDifference();
        this.levels = updateLevels(levels);
        this.quantizer = new UniformQuantizer(this.levels, minDifference, maxDifference);

        decoder = new Decoder(this.image, quantizer, predictor);
    }   

    public void encode()
    {
        Integer a, b, c, prediction;
        Double totalSquaredError = 0.;
        for (Integer i = 1; i < n; ++i)
        {
            for (Integer j = 1; j < m; ++j)
            {
                a = decoder.getPixelAt(i, j - 1);
                b = decoder.getPixelAt(i - 1, j - 1);
                c = decoder.getPixelAt(i - 1, j);
                prediction = predictor.predict(a, b, c);
                quantizedDifferences[i - 1][j - 1] = quantizer.quantize(image[i][j] - prediction);
                decoder.decodePixel(quantizedDifferences[i - 1][j - 1], i, j);
                totalSquaredError += decoder.getPixelAt(i, j) * decoder.getPixelAt(i, j);
            }
        }
        displaySummary(totalSquaredError / (double)(n * m));
    }

    private Predictor getPredictor(PredictorType type)
    {
        Predictor predictor;
        switch (type) 
        {
            case PredictorType.FIRST_ORDER:
                predictor = new FirstOrderPredictor();
                break;
            case PredictorType.SECOND_ORDER:
                predictor = new SecondOrderPredictor();
                break;
            default:
                predictor = new AdaptivePredictor();
                break;
        }
        return predictor;
    }

    private Integer updateLevels(Integer levels)
    {
        if (bits != Math.log(levels) / Math.log(2)) 
        {
            levels = (int)Math.pow(2, bits);
            System.out.println("The levels have been raised to " + levels + " levels");
            return levels;
        }
        return levels;
    }

    private void calculateMinAndMaxDifference()
    {
        minDifference = Integer.MAX_VALUE;
        maxDifference = Integer.MIN_VALUE;

        Integer difference;
        for (Integer i = 1; i < n; ++i)
        {
            for (Integer j = 1; j < m; ++j)
            {
                difference = image[i][j] - predictor.predict(image[i][j - 1], image[i - 1][j - 1], image[i - 1][j]);
                minDifference = Math.min(difference, minDifference);
                maxDifference = Math.max(difference, maxDifference);
            }
        }
    }

    private void displaySummary(Double mse)
    {
        Integer originalImageSize = n * m * 8;
        Integer compressedImageSize = (n - 1) * (m - 1) * bits;
        Integer headerSize = ((n + m - 1) + 138) * 8;

        System.out.println("Quantizer Levels: " + levels);
        System.out.println("MSE: " + mse);
        System.out.println("\nOriginal image size: " + originalImageSize + " bits");
        System.out.println("\nCompressed image size:");
        System.out.println("    Header -> " + headerSize + " bits");
        System.out.println("    Compressed Image -> " + compressedImageSize + " bits");
        System.out.println("    Total -> " + (headerSize + compressedImageSize) + " bits");
        System.out.println("\nCompression ratio (compressed / original): " + ((double)(compressedImageSize + headerSize) / originalImageSize));
    }

    private String toBinaryString(Integer i, Integer nBits)
    {
        String binaryString = "";
        while (nBits > 0)
        {
            binaryString = (i & 1) + binaryString;
            i >>= 1;
            --nBits;
        }
        return binaryString;
    }

    private String getCompressedImageBinaryString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(toBinaryString(n, 32));
        stringBuilder.append(toBinaryString(m, 32));
        stringBuilder.append(toBinaryString(minDifference, 32));
        stringBuilder.append(toBinaryString(maxDifference, 32));
        stringBuilder.append(toBinaryString(levels, 9));
        stringBuilder.append(toBinaryString(predictor.getType(), 2));

        for (Integer i = 0; i < m; ++i)
        {
            stringBuilder.append(toBinaryString(image[0][i], 8));
        }
        for (Integer i = 1; i < n; ++i)
        {
            stringBuilder.append(toBinaryString(image[i][0], 8));
        }

        for (Integer i = 0; i < n - 1; ++i)
        {
            for (Integer j = 0; j < m - 1; ++j)
            {
                stringBuilder.append(toBinaryString(quantizedDifferences[i][j], bits));
            }
        }

        while (stringBuilder.length() % 8 != 0) 
        {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }

    private byte[] binaryStringToBytes(String binaryString)
    {
        byte[] bytes = new byte[binaryString.length() / 8];
        for (Integer i = 0; i < binaryString.length(); i += 8)
        {
            bytes[i / 8] = 0;
            for (Integer j = 0; j < 8; ++j)
            {
                bytes[i / 8] <<= 1;
                bytes[i / 8] |= (binaryString.charAt(i + j) == '0' ? 0 : 1);
            }
        }
        return bytes;
    }

    public void save(String outputPath) throws IOException
    {
        if (!outputPath.endsWith(".bin"))
        {
            outputPath += ".bin";
        }
        String binaryString = getCompressedImageBinaryString();
        byte[] bytes = binaryStringToBytes(binaryString);
        Files.write(Path.of(outputPath), bytes);
    }
}
