package com.github.cfogrady.vb.dim.card;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

public class BemCardReadWriteTest {
    @Test
    void testThatDimWriterWritesIdenticalForSpriteSignedDIM() throws IOException {
        File file = new File("BEM_CARD_IMAGE.bin");
        InputStream fileInputStream = new FileInputStream(file);
        byte[] image = fileInputStream.readAllBytes();
        fileInputStream.close();
        ByteArrayInputStream imageInputStream = new ByteArrayInputStream(image);
        BemCardReader bemCardReader = new BemCardReader();
        BemCard content = bemCardReader.readBemCard(imageInputStream);

        BemCardWriter writer = new BemCardWriter();
        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        writer.writeBemCard(content, imageOutputStream);
        byte[] writtenImage = imageOutputStream.toByteArray();
        Assertions.assertArrayEquals(image, writtenImage);
    }

    @Test
    void testThatSpriteChecksumsMatch() throws IOException {
        BemCardReader reader = new BemCardReader();
        File file = new File("BEM_CARD_IMAGE.bin");
        InputStream fileInputStream = new FileInputStream(file);
        BemCard content = reader.readBemCard(fileInputStream);
        fileInputStream.close();
        content.getSpriteData().getSprites().set(1, DimReaderTest.loadSprite(new File("new_background.bmp")));
        BemCardWriter writer = new BemCardWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer.writeBemCard(content, byteArrayOutputStream);
        byte[] outputData = byteArrayOutputStream.toByteArray();
        //writer.writeBemCard(content, new FileOutputStream(new File("MODIFIED_BEM.bin")));
        BemCard changedSprites = reader.readBemCard(new ByteArrayInputStream(outputData));
        Assertions.assertArrayEquals(content.getSpriteData().getSpriteChecksums().toArray(new Integer[0]), changedSprites.getSpriteData().getSpriteChecksums().toArray(new Integer[0]));
        Assertions.assertEquals(0x400000, outputData.length);
    }
}
