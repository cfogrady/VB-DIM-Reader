package com.github.cfogrady.vb.dim.reader;

import com.github.cfogrady.vb.dim.reader.content.DimContent;
import com.github.cfogrady.vb.dim.reader.reader.DimReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

@Slf4j
public class DimReaderTest {
    @Test
    void testThatDimReaderWorks() throws IOException {
        DimReader reader = new DimReader();
        File file = new File("file.bin");
        InputStream fileInputStream = new FileInputStream(file);
        reader.readDimData(fileInputStream, false);
    }

    @Test
    void testThatSpriteChecksumsMatch() throws IOException {
        DimReader reader = new DimReader();
        File file = new File("original.bin");
        InputStream fileInputStream = new FileInputStream(file);
        DimContent content = reader.readDimData(fileInputStream, false);
        fileInputStream.close();
        file = new File("modified.bin");
        fileInputStream = new FileInputStream(file);
        DimContent englishContent = reader.readDimData(fileInputStream, false);
        fileInputStream.close();
        Assertions.assertArrayEquals(content.getSpriteData().getSpriteChecksums().toArray(new Integer[0]), englishContent.getSpriteData().getSpriteChecksums().toArray(new Integer[0]));
    }
}
