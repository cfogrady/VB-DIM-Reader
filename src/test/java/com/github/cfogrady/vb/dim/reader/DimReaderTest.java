package com.github.cfogrady.vb.dim.reader;

import com.github.cfogrady.vb.dim.reader.reader.DimReader;
import org.junit.jupiter.api.Test;

import java.io.*;

public class DimReaderTest {
    @Test
    void testThatDimReaderWorks() throws IOException {
        DimReader reader = new DimReader();
        File file = new File("C:\\dev\\Digimon Hacking\\01._Agumon_EDBEEEBB.bin");
        InputStream fileInputStream = new FileInputStream(file);
        reader.readDimData(fileInputStream, false);
    }
}
