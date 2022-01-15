package com.github.cfogrady.vb.dim.reader;


import com.github.cfogrady.vb.dim.reader.content.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Slf4j
public class DimReader {
    public DimContent readDimData(InputStream inputStream, boolean strictEmulation) {
        if(strictEmulation) {
            return readDimData(inputStream, 17, 15, 1, true);
        } else {
            return readDimData(inputStream, null, null, null, false);
        }
    }

    public DimContent readDimData(InputStream inputStream, Integer maxStatSlots, Integer maxAdventures, Integer maxDimSpecificFusions, boolean verifyChecksum) {
        ChecksumBuilder checksumBuilder = new ChecksumBuilder();
        try {
            InputStreamWithNot inputStreamWithNot = InputStreamWithNot.wrap(inputStream, checksumBuilder);
            byte[] bytes = inputStreamWithNot.readNBytes(0x1030);
            DimHeader header = DimHeaderReader.dimHeaderFromBytes(bytes);
            inputStreamWithNot.readToOffset(0x30000); // skip everything until stats section
            bytes = inputStreamWithNot.readToOffset(0x40000);
            DimStats stats = DimStatsReader.dimStatsFromBytes(bytes, maxStatSlots);
            bytes = inputStreamWithNot.readToOffset(0x50000);
            DimEvolutionRequirements evolutionRequirements = DimEvolutionsReader.dimEvolutionRequirementsFromBytes(bytes);
            bytes = inputStreamWithNot.readToOffset(0x60000);
            DimAdventures adventures = DimAdventuresReader.dimAdventuresFromBytes(bytes, maxAdventures);
            byte[] spriteDimensions = inputStreamWithNot.readToOffset(0x70000);
            bytes = inputStreamWithNot.readToOffset(0x80000);
            DimFusions fusions = DimFusionsReader.dimFusionsFromBytes(bytes);
            bytes = inputStreamWithNot.readToOffset(0x100000);
            DimSpecificFusions dimSpecificFusions = DimSpecificFusionsReader.dimSpecificFusionsFromBytes(bytes, maxDimSpecificFusions);
            SpriteData spriteData = DimSpritesReader.spriteDataFromBytesAndStream(spriteDimensions, inputStreamWithNot);
            inputStreamWithNot.readToOffset(0x3FFFFE);
            bytes = inputStreamWithNot.readNBytes(2);
            int dimChecksum = ByteUtils.getUnsigned16Bit(bytes)[0];
            int calculatedChecksum = checksumBuilder.getCheckSum();
            if(dimChecksum != calculatedChecksum) {
                log.warn("Checksums don't match!");
                if(verifyChecksum) {
                    throw new IllegalStateException("Invalid Dim. Calculated checksum doesn't match Dim checksum");
                }
            }
            return DimContent.builder()
                    .dimHeader(header)
                    .dimStats(stats)
                    .dimEvolutionRequirements(evolutionRequirements)
                    .dimAdventures(adventures)
                    .dimFusions(fusions)
                    .dimSpecificFusion(dimSpecificFusions)
                    .spriteData(spriteData)
                    .checksum(dimChecksum)
                    .calculatedCheckSum(calculatedChecksum)
                    .build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
