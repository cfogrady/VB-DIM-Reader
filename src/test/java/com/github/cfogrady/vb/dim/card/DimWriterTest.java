package com.github.cfogrady.vb.dim.card;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

public class DimWriterTest {

    @Test
    void testThatDimWriterWritesIdenticalForNormalDIM() throws IOException {
        File file = new File("C:\\dev\\Digimon Hacking\\01._Agumon_Original_BE86D5FD.bin");
        InputStream fileInputStream = new FileInputStream(file);
        byte[] image = fileInputStream.readAllBytes();
        fileInputStream.close();
        ByteArrayInputStream dimInputStream = new ByteArrayInputStream(image);
        DimReader dimReader = new DimReader();
        DimCard content = dimReader.readDimData(dimInputStream, true);

        DimWriter writer = new DimWriter();
        ByteArrayOutputStream dimOutputStream = new ByteArrayOutputStream();
        writer.writeDimData(content, dimOutputStream);
        byte[] writtenImage = dimOutputStream.toByteArray();
        Assertions.assertArrayEquals(image, writtenImage);
    }
}
