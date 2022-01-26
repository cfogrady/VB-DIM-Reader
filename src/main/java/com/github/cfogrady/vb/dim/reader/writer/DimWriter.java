package com.github.cfogrady.vb.dim.reader.writer;


import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import com.github.cfogrady.vb.dim.reader.content.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

@Slf4j
public class DimWriter {

    public static final int NONE_VALUE = 65535;

    public void writeDimData(DimContent dimContent, OutputStream outputStream) {
        writeDimData(dimContent, outputStream, false);
    }

    public void writeDimData(DimContent dimContent, OutputStream outputStream, boolean strictEmulation) {
        ChecksumBuilder checksumBuilder = new ChecksumBuilder();
        OutputStreamWithNot outputStreamWithNot = OutputStreamWithNot.wrap(outputStream, checksumBuilder);
        try {
            HeaderWriter.writeHeader(dimContent.getDimHeader(), outputStreamWithNot);
            StatsWriter.writeStats(dimContent.getDimStats(), outputStreamWithNot, strictEmulation);
            EvolutionsWriter.writeEvolutions(dimContent.getDimEvolutionRequirements(), outputStreamWithNot, strictEmulation);
            AdventuresWriter.writeAdventures(dimContent.getDimAdventures(), outputStreamWithNot, strictEmulation);
            SpriteDimentionsWriter.writeSpriteDimensions(dimContent.getSpriteData(), outputStreamWithNot);
            FusionsWriter.writeFusions(dimContent.getDimFusions(), outputStreamWithNot, strictEmulation);
            SpecificFusionsWriter.writeSpecificFusions(dimContent.getDimSpecificFusion(), outputStreamWithNot, strictEmulation);
            SpriteWriter.writeSpriteData(dimContent.getSpriteData(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(0x3ffffe);
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(outputStreamWithNot.getChecksum()));
        } catch (IOException e) {
            log.error("Failed to write DIM file!", e);
            throw new UncheckedIOException(e);
        }
    }
}
