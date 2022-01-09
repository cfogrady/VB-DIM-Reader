package com.github.cfogrady.vb.dim.reader;


import com.github.cfogrady.vb.dim.reader.content.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class DimReader {
    public DimContent readDimData(InputStream inputStream, boolean strictEmulation) {
        if(strictEmulation) {
            return readDimData(inputStream, 17, 15, 1);
        } else {
            return readDimData(inputStream, null, null, null);
        }
    }

    public DimContent readDimData(InputStream inputStream, Integer maxStatSlots, Integer maxAdventures, Integer maxDimSpecificFusions) {
        ChecksumBuilder checksumBuilder = new ChecksumBuilder();
        try {
            byte[] bytes = ByteUtils.applyNotOperation(inputStream.readNBytes(0x102F));
            DimHeader header = DimHeader.dimHeaderFromBytes(bytes, checksumBuilder);
            inputStream.readNBytes(0x30000 - 0x102F); // skip everything until stats section
            bytes = ByteUtils.applyNotOperation(inputStream.readNBytes(0x40000 - 0x30000));
            DimStats stats = DimStats.dimStatsFromBytes(bytes, checksumBuilder, maxStatSlots);
            bytes = ByteUtils.applyNotOperation(inputStream.readNBytes(0x50000 - 0x40000));
            DimEvolutionRequirements evolutionRequirements = DimEvolutionRequirements.dimEvolutionRequirementsFromBytes(bytes, checksumBuilder);
            bytes = ByteUtils.applyNotOperation(inputStream.readNBytes(0x60000 - 0x50000));
            DimAdventures adventures = DimAdventures.dimAdventuresFromBytes(bytes, checksumBuilder, maxAdventures);
            byte[] spriteDimensions = ByteUtils.applyNotOperation(inputStream.readNBytes(0x70000 - 0x60000));
            bytes = ByteUtils.applyNotOperation(inputStream.readNBytes(0x80000 - 0x70000));
            DimFusions fusions = DimFusions.dimFusionsFromBytes(bytes, checksumBuilder);
            bytes = ByteUtils.applyNotOperation(inputStream.readNBytes(0x100000 - 0x80000));
            DimSpecificFusions dimSpecificFusions = DimSpecificFusions.dimSpecificFusionsFromBytes(bytes, checksumBuilder, maxDimSpecificFusions);
            SpriteData spriteData = SpriteData.spriteDataFromBytesAndStream(spriteDimensions, inputStream, checksumBuilder);
            return DimContent.builder()
                    .dimHeader(header)
                    .dimStats(stats)
                    .dimEvolutionRequirements(evolutionRequirements)
                    .dimAdventures(adventures)
                    .dimFusions(fusions)
                    .dimSpecificFusion(dimSpecificFusions)
                    .spriteData(spriteData)
                    .checksum(checksumBuilder.getCheckSum())
                    .build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
