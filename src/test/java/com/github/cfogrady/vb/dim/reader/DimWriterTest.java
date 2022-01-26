package com.github.cfogrady.vb.dim.reader;

import com.github.cfogrady.vb.dim.reader.content.DimContent;
import com.github.cfogrady.vb.dim.reader.reader.DimReader;
import com.github.cfogrady.vb.dim.reader.writer.DimWriter;
import org.junit.jupiter.api.Test;

import java.io.*;

public class DimWriterTest {
    @Test
    void testThatDimReaderWorks() throws IOException {
        DimReader reader = new DimReader();
        File file = new File("C:\\dev\\Digimon Hacking\\01._Agumon_EDBEEEBB.bin");
        InputStream fileInputStream = new FileInputStream(file);
        DimContent content = reader.readDimData(fileInputStream, false);
        fileInputStream.close();

        DimWriter writer = new DimWriter();
        file = new File("testDIM.bin");
        OutputStream outputStream = new FileOutputStream(file, false);
        writer.writeDimData(content, outputStream);
        outputStream.flush();
        outputStream.close();

    }
}
