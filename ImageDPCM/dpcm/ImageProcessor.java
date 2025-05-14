package dpcm;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageProcessor {
    public static Integer[][][] loadImage(String imagePath) throws IOException
    {
        File file = new File(imagePath);
        BufferedImage bufferedimage = ImageIO.read(file);

        Integer n = bufferedimage.getHeight();
        Integer m = bufferedimage.getWidth();
        Integer[][][] image = new Integer[n][m][3];

        for (Integer i = 0; i < n; ++i)
        {
            for (Integer j = 0; j < m; ++j)
            {
                Integer pixel = bufferedimage.getRGB(j, i);
                for (Integer k = 2; k >= 0; --k)
                {
                    image[i][j][k] = pixel & 0xff;
                    pixel >>= 8; 
                }
            }
        }

        return image;
    }

    public static Integer[][] rgbToGray(Integer[][][] image)
    {
        Integer n = image.length;
        Integer m = image[0].length;
        Integer[][] grayImage = new Integer[n][m];

        for (Integer i = 0; i < n; ++i)
        {
            for (Integer j = 0; j < m; ++j)
            {
                grayImage[i][j] = (image[i][j][0] + image[i][j][1] + image[i][j][2]) / 3;
            }
        }

        return grayImage;
    }

    public static void saveGrayImage(Integer[][] image, String outputPath) throws IOException
    {
        Integer n = image.length;
        Integer m = image[0].length;

        BufferedImage bufferedImage = new BufferedImage(m, n, BufferedImage.TYPE_INT_RGB);
        for (Integer i = 0; i < n; ++i)
        {
            for (Integer j = 0; j < m; ++j)
            {
                Integer pixel = (image[i][j] << 16) | (image[i][j] << 8) | image[i][j];
                bufferedImage.setRGB(j, i, pixel);
            }
        }
        File file = new File(outputPath);
        ImageIO.write(bufferedImage, "jpg", file);
    }
}
