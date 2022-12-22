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
}
