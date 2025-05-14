package dpcm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import dpcm.predictor.AdaptivePredictor;
import dpcm.predictor.FirstOrderPredictor;
import dpcm.predictor.SecondOrderPredictor;
import dpcm.predictor.Predictor;

public class Decoder 
{
    private Integer[][] image;
    private Integer n;
    private Integer m;
    private UniformQuantizer quantizer;
    private Predictor predictor;
    private Integer[][] quantizedDifferences;

    public Decoder(Integer[][] image, UniformQuantizer quantizer, Predictor predictor)
    {
        this.quantizer = quantizer;
        this.predictor = predictor;
        n = image.length;
        m = image[0].length;
        this.image = new Integer[n][m];
        for (Integer i = 0; i < m; ++i) 
        {
            this.image[0][i] = image[0][i];
        }
        for (Integer i = 1; i < n; ++i)
        {
            this.image[i][0] = image[i][0];
        }
    }

    public Decoder(String imagePath) throws IOException
    {
        byte[] bytes = Files.readAllBytes(Path.of(imagePath));
        StringBuilder binaryString = new StringBuilder();
        for (byte b : bytes)
        {
            binaryString.append(toBinaryString(b));
        }
        parseBinaryString(binaryString);
    }

    private void parseBinaryString(StringBuilder binaryString)
    {
        n = Integer.parseInt(binaryString.substring(0, 32), 2);

        m = Integer.parseInt(binaryString.substring(32, 64), 2);

        Integer minDifference = Integer.parseInt("0" + binaryString.substring(65, 96), 2);
        minDifference |= (binaryString.charAt(64) == '0' ? 0 : 1) << 31;
        
        Integer maxDifference = Integer.parseInt("0" + binaryString.substring(97, 128), 2);
        maxDifference |= (binaryString.charAt(96) == '0' ? 0 : 1) << 31;

        Integer levels = Integer.parseInt(binaryString.substring(128, 137), 2);
        Integer bits = (int)(Math.log(levels) / Math.log(2));

        quantizer = new UniformQuantizer(levels, minDifference, maxDifference);

        Integer predictorType = Integer.parseInt(binaryString.substring(137, 139), 2);
        switch (predictorType) 
        {
            case 0:
                predictor = new FirstOrderPredictor();
                break;
            case 1:
                predictor = new SecondOrderPredictor();
                break;
            default:
                predictor = new AdaptivePredictor();
                break;
        }
            
        image = new Integer[n][m];
        quantizedDifferences = new Integer[n - 1][m - 1];
        
        Integer current = 139;
        for (Integer i = 0; i < m; ++i)
        {
            image[0][i] = Integer.parseInt(binaryString.substring(current, current + 8), 2);
            current += 8;
        }
            
        for (Integer i = 1; i < n; ++i)
        {
            image[i][0] = Integer.parseInt(binaryString.substring(current, current + 8), 2);
            current += 8;
        }
        
        for (Integer i = 0; i < n - 1; ++i)
        {
            for (Integer j = 0; j < m - 1; ++j)
            {
                quantizedDifferences[i][j] = Integer.parseInt(binaryString.substring(current, current + bits), 2);
                current += bits;
            }
        }
    }

    private String toBinaryString(byte b)
    {
        String binaryString = "";
        Integer i = 0;
        while (i < 8)
        {
            binaryString = (b & 1) + binaryString;
            b >>= 1;
            ++i;
        }
        return binaryString;
    }

    public void decodePixel(Integer quantizedDifference, Integer i, Integer j)
    {
        Integer prediction = predictor.predict(image[i][j - 1], image[i - 1][j - 1], image[i - 1][j]);
        Integer dequantizedDifference = quantizer.dequantize(quantizedDifference);
        image[i][j] = prediction + dequantizedDifference;
        image[i][j] = Math.min(255, image[i][j]);
        image[i][j] = Math.max(0, image[i][j]);
    }

    public void decode()
    {
        for (Integer i = 0; i < n - 1; ++i)
        {
            for (Integer j = 0; j < m - 1; ++j)
            {
                decodePixel(quantizedDifferences[i][j], i + 1, j + 1);
            }
        }
    }

    public Integer getPixelAt(Integer i, Integer j)
    {
        return image[i][j];
    }

    public Integer[][] getImage()
    {
        Integer[][] image = new Integer[n][m];
        for (Integer i = 0; i < n; ++i)
        {
            for (Integer j = 0; j < m; ++j)
            {
                image[i][j] = this.image[i][j];
            }
        }
        return image;
    }

    public void save(String outputPath) throws IOException
    {
        if (!outputPath.endsWith(".jpg"))
        {
            outputPath += ".jpg";
        }
        ImageProcessor.saveGrayImage(image, outputPath);
    }
}
