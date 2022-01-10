package com.github.cfogrady.vb.dim.reader;

import org.junit.jupiter.api.Test;

import java.io.*;

public class DimReaderTest {
    @Test
    void testThatDimReaderWorks() throws IOException {
        DimReader reader = new DimReader();
        File file = new File("DIM.DIM");
        InputStream fileInputStream = new FileInputStream(file);
        reader.readDimData(fileInputStream, false);
    }
}
