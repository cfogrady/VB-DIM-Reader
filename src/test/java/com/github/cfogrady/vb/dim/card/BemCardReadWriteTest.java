package com.github.cfogrady.vb.dim.card;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

public class BemCardReadWriteTest {

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

    @Test
    void testThatSpriteChecksumsMatchTwoCards() throws IOException {
        BemCardReader reader = new BemCardReader();
        File file = new File("C:\\dev\\Digimon Hacking\\BE Memories\\gammamon-bememory-backup-20221202104526.bin");
        InputStream fileInputStream = new FileInputStream(file);
        BemCard card1 = reader.readBemCard(fileInputStream);
        fileInputStream.close();
        File file2 = new File("C:\\Users\\cfogr\\Downloads\\LessSpritesTest");
        InputStream fileInputStream2 = new FileInputStream(file2);
        BemCard card2 = reader.readBemCard(fileInputStream2);
        Assertions.assertArrayEquals(card1.getSpriteData().getSpriteChecksums().toArray(new Integer[0]), card2.getSpriteData().getSpriteChecksums().toArray(new Integer[0]));
    }
}
