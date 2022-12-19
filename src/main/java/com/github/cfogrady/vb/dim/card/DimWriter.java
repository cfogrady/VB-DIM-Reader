package com.github.cfogrady.vb.dim.card;


import com.github.cfogrady.vb.dim.adventure.AdventuresWriter;
import com.github.cfogrady.vb.dim.character.StatsWriter;
import com.github.cfogrady.vb.dim.fusion.FusionsWriter;
import com.github.cfogrady.vb.dim.fusion.SpecificFusionsWriter;
import com.github.cfogrady.vb.dim.header.HeaderWriter;
import com.github.cfogrady.vb.dim.sprite.SpriteDimentionsWriter;
import com.github.cfogrady.vb.dim.sprite.SpriteWriter;
import com.github.cfogrady.vb.dim.transformation.EvolutionsWriter;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.DIMChecksumBuilder;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

@Slf4j
public class DimWriter {

    public static final int NONE_VALUE = DimReader.NONE_VALUE;

    public void writeDimData(DimCard dimContent, OutputStream outputStream) {
        DIMChecksumBuilder checksumBuilder = new DIMChecksumBuilder();
        OutputStreamWithNot outputStreamWithNot = OutputStreamWithNot.wrap(outputStream, checksumBuilder);
        try {
            HeaderWriter.writeHeader(dimContent.getDimHeader(), outputStreamWithNot);
            StatsWriter.writeStats(dimContent.getDimStats(), outputStreamWithNot);
            EvolutionsWriter.writeEvolutions(dimContent.getDimEvolutionRequirements(), outputStreamWithNot);
            AdventuresWriter.writeAdventures(dimContent.getDimAdventures(), outputStreamWithNot);
            SpriteDimentionsWriter.writeSpriteDimensions(dimContent.getSpriteData(), outputStreamWithNot);
            FusionsWriter.writeFusions(dimContent.getDimFusions(), outputStreamWithNot);
            SpecificFusionsWriter.writeSpecificFusions(dimContent.getDimSpecificFusion(), outputStreamWithNot);
            SpriteWriter.writeSpriteData(dimContent.getSpriteData(), dimContent.getDimHeader().hasSpriteSignature(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(0x3ffffe);
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(outputStreamWithNot.getChecksum()));
        } catch (IOException e) {
            log.error("Failed to write DIM file!", e);
            throw new UncheckedIOException(e);
        }
    }
}