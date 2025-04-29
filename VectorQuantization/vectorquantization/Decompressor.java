package vectorquantization;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import rgbmatrix.RGBMatrix;

public class Decompressor 
{
    private RGBMatrix imageMatrix;
    private Double[][][][] codebook;
    private ArrayList<RGBMatrix> blocks;
    private Integer bits;
    private Integer blockSize;

    public Decompressor(String filePath) throws IOException
    {
        byte[] bytes = Files.readAllBytes(Path.of(filePath));
        StringBuilder binaryString = parseBytes(bytes);
        reconstructImage(binaryString);
    }   

    private String toBinaryString(byte b)
    {
        String binaryString = "";
        for (int i = 0; i < 8; ++i)
        {
            binaryString = ((b & 1) == 0 ? "0" : "1") + binaryString;
            b >>= 1;
        }   
        return binaryString;
    }

    private StringBuilder parseBytes(byte[] bytes)
    {
        StringBuilder binaryString = new StringBuilder();
        for (byte b : bytes)
        {
            binaryString.append(toBinaryString(b));
        }
        Integer nCodebooks = Integer.parseInt(binaryString.substring(0, 32), 2);
        binaryString.delete(0, 32);
        blockSize = Integer.parseInt(binaryString.substring(0, 32), 2);
        binaryString.delete(0, 32);
        Integer blocksY = Integer.parseInt(binaryString.substring(0, 32), 2);
        binaryString.delete(0, 32);
        Integer blocksX = Integer.parseInt(binaryString.substring(0, 32), 2);
        binaryString.delete(0, 32);

        codebook = new Double[nCodebooks][blockSize][blockSize][3];
        imageMatrix = new RGBMatrix(blocksX * blockSize, blocksY * blockSize);
        blocks = imageMatrix.chunkBlocks(blockSize);
        bits = (int)Math.ceil(Math.log(nCodebooks + 1) / Math.log(2));

        return binaryString;
    }

    private void reconstructImage(StringBuilder binaryString)
    {
        for (Integer c = 0; c < codebook.length; ++c)
        {
            for (Integer i = 0; i < blockSize; ++i)
            {
                for (Integer j = 0; j < blockSize; ++j)
                {
                    for (Integer k = 0; k < 3; ++k)
                    {
                        codebook[c][i][j][k] = (double)Integer.parseInt(binaryString.substring(0, 8), 2);
                        binaryString.delete(0, 8);
                    }
                }
            }
        }
        for (Integer i = 0; i < blocks.size(); ++i)
        {
            Integer qDash = Integer.parseInt(binaryString.substring(0, bits), 2);
            binaryString.delete(0, bits);
            blocks.get(i).update(codebook[qDash]);
        }
    }

    public void save(String outputPath) throws IOException
    {
        if (!outputPath.endsWith("jpg"))
        {
            outputPath += ".jpg";
        }
        BufferedImage image = imageMatrix.getImage();
        File outputFile = new File(outputPath);
        ImageIO.write(image, "jpg",  outputFile);
    }
}
