package com.github.cfogrady.vb.dim.card;

import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
public class DimReaderTest {
    @Test
    void testThatDimReaderWorks() throws IOException {
        DimReader reader = new DimReader();
        File file = new File("original.bin");
        InputStream fileInputStream = new FileInputStream(file);
        reader.readDimData(fileInputStream, false);
    }

    @Test
    void testThatSpriteChecksumsMatch() throws IOException {
        DimReader reader = new DimReader();
        File file = new File("original.bin");
        InputStream fileInputStream = new FileInputStream(file);
        DimCard content = reader.readDimData(fileInputStream, false);
        fileInputStream.close();
        content.getSpriteData().getSprites().set(1, loadSprite(new File("new_background.bmp")));
        DimWriter writer = new DimWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer.writeDimData(content, byteArrayOutputStream);
        byte[] outputData = byteArrayOutputStream.toByteArray();
        DimCard changedSprites = reader.readDimData(new ByteArrayInputStream(outputData), false);
        Assertions.assertArrayEquals(content.getSpriteData().getSpriteChecksums().toArray(new Integer[0]), changedSprites.getSpriteData().getSpriteChecksums().toArray(new Integer[0]));
        Assertions.assertEquals(0x400000, outputData.length);
    }

    public static SpriteData.Sprite loadSprite(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] pixelData = convertToR5G6B5(image);
        SpriteData.Sprite sprite = SpriteData.Sprite.builder().width(width).height(height).pixelData(pixelData).build();
        return sprite;
    }

    private static byte[] convertToR5G6B5(BufferedImage image) {
        byte[] bytes = new byte[image.getWidth()*image.getHeight()*2];
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                Color color = new Color(pixel);
                int red = (int) Math.floor(color.getRed() * 31.0 / 255.0);
                int green = (int) Math.floor(color.getGreen() * 63.0 / 255.0);
                int blue = (int) Math.floor(color.getBlue() * 31.0 / 255.0);
                // RRRRRGGG GGGBBBBB
                byte byte0 = (byte) (((red & 0xFF) << 3) | ((green & 0xFF) >> 3));
                byte byte1 = (byte) (((green & 0xFF) << 5) | (blue & 0xFF));
                int index = (y * image.getWidth() + x) * 2;
                bytes[index] = byte1;
                bytes[index + 1] = byte0;
            }
        }
        return bytes;
    }
}
