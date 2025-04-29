package rgbmatrix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class RGBMatrix 
{
    private Integer width;
    private Integer height;
    private Integer[][][] image;
    private Integer offX;
    private Integer offY;

    public RGBMatrix(String imagePath) throws IOException
    {
        File file = new File(imagePath);
        BufferedImage bufferedImage = ImageIO.read(file);
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        offX = 0;
        offY = 0;
        image = new Integer[height][width][3];
        for (Integer i = 0; i < height; ++i)
        {
            for (Integer j = 0; j < width; ++j)
            {
                Integer pixel = bufferedImage.getRGB(j, i);
                for (Integer k = 2; k >= 0; --k)
                {
                    image[i][j][k] = pixel & 0xFF;
                    pixel >>= 8;
                }
            }
        }
    }

    public RGBMatrix(Integer width, Integer height)
    {
        this.width = width;
        this.height = height;
        offX = 0;
        offY = 0;
        image = new Integer[height][width][3];
        for (Integer i = 0; i < height; ++i)
        {
            for (Integer j = 0; j < width; ++j)
            {
                for (Integer k = 0; k < 3; ++k)
                {
                    image[i][j][k] = 0;   
                }
            }
        }
    }
    
    public RGBMatrix(RGBMatrix mat, Integer blockSize, Integer x, Integer y)
    {
        image = mat.image;
        offX = x;
        offY = y;
        width = blockSize;
        height = blockSize;
    }

    public ArrayList<RGBMatrix> chunkBlocks(Integer blockSize)
    {
        Integer xBlocks = width / blockSize, yBlocks = height / blockSize;
        if (xBlocks == 0 || yBlocks == 0)
        {
            return null;
        }
        RGBMatrix[] blocks = new RGBMatrix[xBlocks * yBlocks];
        for (Integer i = 0; i < yBlocks; ++i)
        {
            for (Integer j = 0; j < xBlocks; ++j)
            {
                blocks[i * xBlocks + j] = new RGBMatrix(this, blockSize, j * blockSize, i * blockSize);
            }
        }
        return new ArrayList<RGBMatrix>(Arrays.asList(blocks));
    }

    public static Double[][][] mean(ArrayList<RGBMatrix> matList, Integer width, Integer height)
    {
        Double n = (double)matList.size();
        Double[][][] mat = new Double[height][width][3];
        for (Integer i = 0; i < height; ++i)
        {
            for (Integer j = 0; j < width; ++j)
            {
                for (Integer k = 0; k < 3; ++k)
                {
                    mat[i][j][k] = 0.;
                }
            }
        }
        if (n == 0)
        {
            return mat;
        } 
        for (Integer c = 0; c < n; ++c)
        {
            RGBMatrix block = matList.get(c);
            for (Integer i = 0; i < height; ++i)
            {
                for (Integer j = 0; j < width; ++j)
                {
                    for (Integer k = 0; k < 3; ++k)
                    {
                        mat[i][j][k] += block.image[block.offY + i][block.offX + j][k] / n;
                    }
                }
            }
        }
        return mat;
    }

    public Integer squaredDifferences(Double[][][] matrix, Integer n, Integer m)
    {
        if (n != height && m != width) return Integer.MAX_VALUE;
        Integer sumSquaredDifferences = 0;
        for (Integer i = 0; i < n; ++i)
        {
            for (Integer j = 0; j < m; ++j)
            {
                for (Integer k = 0; k < 3; ++k)
                {
                    sumSquaredDifferences += (int)((image[offY + i][offX + j][k] - matrix[i][j][k]) * (image[offY + i][offX + j][k] - matrix[i][j][k]));
                }
            }
        }
        return sumSquaredDifferences;
    }

    public Double ecludianDistance(Double[][][] matrix, Integer n, Integer m)
    {
        return Math.sqrt(squaredDifferences(matrix, n, m));
    }

    public void update(Double[][][] matrix)
    {
        for (Integer i = 0; i < height; ++i)
        {
            for (Integer j = 0; j < width; ++j)
            {
                for (Integer k = 0; k < 3; ++k)
                {
                    image[i + offY][j + offX][k] = (int)(double)matrix[i][j][k];
                }
            }
        }
    }

    public BufferedImage getImage()
    {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (Integer i = 0; i < height; ++i)
        {
            for (Integer j = 0; j < width; ++j)
            {
                Integer pixel = 0;
                for (Integer k = 0; k < 3; ++k)
                {
                    pixel <<= 8;
                    pixel |= image[i + offY][j + offX][k];
                }
                bufferedImage.setRGB(j, i, pixel);
            }
        }
        return bufferedImage;
    }

    public Double mse(Double[][][] matrix, Integer n, Integer m)
    {
        return squaredDifferences(matrix, n, m) / (n * m * 3.);
    }

    public Integer getWidth()
    {
        return width;
    }

    public Integer getHeight()
    {
        return height;
    }

    @Override
    public String toString() {
        String str = "[\n";
        for (Integer i = offY; i < offY + height; ++i)
        {
            for (Integer j = offX; j < offX + width; ++j)
            {
                str += "[" + image[i][j][0] + " " + image[i][j][1] + " " + image[i][j][2] + "]";
            }
            str += "\n";
        }
        str += "]";
        return str;
    }

}
