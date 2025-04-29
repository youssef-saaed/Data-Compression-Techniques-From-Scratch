package vectorquantization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import rgbmatrix.Clustering;
import rgbmatrix.RGBMatrix;

public class Compressor {
    private RGBMatrix imageMatrix;
    private Double[][][][] codebook;
    private ArrayList<RGBMatrix> blocks;
    private Integer bits;
    private Integer blockSize;

    public Compressor(String imagePath, Integer nCodebooks, Integer codebookSize) throws IOException
    {
        bits = (int)Math.ceil(Math.log(nCodebooks + 1) / Math.log(2));
        if ((int)Math.pow(2, bits) - 1 != nCodebooks)
        {
            nCodebooks = (int)Math.pow(2, bits) - 1;
            System.out.println("Number of codebooks has been updated to " + nCodebooks);
        }
        imageMatrix = new RGBMatrix(imagePath);
        blocks = imageMatrix.chunkBlocks(codebookSize);
        codebook = Clustering.cluster(nCodebooks, blocks, codebookSize, codebookSize, 1.); 
        blockSize = codebookSize;
    }

    private String toBinaryString(Integer i, Integer nBits)
    {
        String binaryString = "";
        while (nBits > 0)
        {
            binaryString = ((i & 1) == 1 ? "1" : "0") + binaryString;
            i >>= 1;
            --nBits;
        }
        return binaryString;
    }

    private Integer[] quantizeBlocks()
    {
        Integer[] q = new Integer[blocks.size()];
        for (Integer i = 0; i < blocks.size(); ++i)
        {
            Double minDistance = blocks.get(i).ecludianDistance(codebook[0], blockSize, blockSize);
            Integer minI = 0;
            for (Integer k = 0; k < codebook.length; ++k)
            {
                Double distance = blocks.get(i).ecludianDistance(codebook[k], blockSize, blockSize);
                if (distance < minDistance)
                {
                    minDistance = distance;
                    minI = k;
                }
            }
            q[i] = minI;
        }
        return q;
    }

    private String buildBinaryString()
    {
        StringBuilder binaryString = new StringBuilder();
        binaryString.append(toBinaryString(codebook.length, 32));
        binaryString.append(toBinaryString(codebook[0].length, 32));
        binaryString.append(toBinaryString(blocks.size() / (imageMatrix.getWidth() / blockSize), 32));
        binaryString.append(toBinaryString(blocks.size() / (imageMatrix.getHeight()  / blockSize), 32));
        for (Integer b = 0; b < codebook.length; ++b)
        {
            for (Integer i = 0; i < codebook[0].length; ++i)
            {
                for (Integer j = 0; j < codebook[0].length; ++j)
                {
                    for (Integer k = 0; k < 3; ++k)
                    {
                        binaryString.append(toBinaryString((int)(double)codebook[b][i][j][k], 8));
                    }
                }
            }
        }
        Integer[] q = quantizeBlocks();
        for (Integer i : q)
        {
            binaryString.append(toBinaryString(i, bits));
        } 
        return binaryString.toString();
    }

    private byte[] binaryStringToBytes(String binaryString)
    {
        while (binaryString.length() % 8 != 0) 
        {
            binaryString += '0';
        }
        byte[] bytes = new byte[binaryString.length() / 8];
        for (Integer i = 0; i < binaryString.length(); i += 8)
        {
            bytes[i / 8] = (byte)Integer.parseInt(binaryString.substring(i, i + 8), 2);
        }
        return bytes;
    }

    public void save(String outputPath) throws IOException
    {
        String binaryString = buildBinaryString();
        byte[] bytes = binaryStringToBytes(binaryString);
        Files.write(Path.of(outputPath), bytes);
    }
}
