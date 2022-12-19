package com.github.cfogrady.vb.dim.card;


import com.github.cfogrady.vb.dim.adventure.DimAdventures;
import com.github.cfogrady.vb.dim.adventure.DimAdventuresReader;
import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.character.DimStatsReader;
import com.github.cfogrady.vb.dim.fusion.DimFusions;
import com.github.cfogrady.vb.dim.fusion.DimFusionsReader;
import com.github.cfogrady.vb.dim.fusion.DimSpecificFusions;
import com.github.cfogrady.vb.dim.fusion.DimSpecificFusionsReader;
import com.github.cfogrady.vb.dim.header.DimHeader;
import com.github.cfogrady.vb.dim.header.DimHeaderReader;
import com.github.cfogrady.vb.dim.sprite.DimSpritesReader;
import com.github.cfogrady.vb.dim.transformation.DimEvolutionsReader;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.DIMChecksumBuilder;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.DimEvolutionRequirements;
import com.github.cfogrady.vb.dim.util.InputStreamWithNot;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Slf4j
public class DimReader {

    public static final int NONE_VALUE = 65535;

    private final BemCardReader bemCardReader;

    public DimReader() {
        this.bemCardReader = new BemCardReader();
    }

    public DimReader(BemCardReader bemCardReader) {
        this.bemCardReader = bemCardReader;
    }

    public Card readDimCardData(InputStream inputStream, boolean verifyChecksum) {
        DIMChecksumBuilder checksumBuilder = new DIMChecksumBuilder();
        InputStreamWithNot inputStreamWithNot = new InputStreamWithNot(inputStream, checksumBuilder);
        try {
            byte[] headerBytes = inputStreamWithNot.readNBytes(0x1030);
            if(hasBEMFlags(headerBytes)) {
                return bemCardReader.readBemCard(headerBytes, inputStreamWithNot);
            } else {
                return readDimData(inputStreamWithNot, headerBytes, verifyChecksum);
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private boolean hasBEMFlags(byte[] bytes) {
        for(int i = 0x1000; i < 0x1010; i++) {
            if(bytes[i] != 0) {
                return true;
            }
        }
        return false;
    }

    public DimCard readDimData(InputStream inputStream, boolean verifyChecksum) {
        DIMChecksumBuilder checksumBuilder = new DIMChecksumBuilder();
        InputStreamWithNot inputStreamWithNot = InputStreamWithNot.wrap(inputStream, checksumBuilder);
        try {
            byte[] bytes = inputStreamWithNot.readNBytes(0x1030);
            return readDimData(inputStreamWithNot, bytes, verifyChecksum);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private DimCard readDimData(InputStreamWithNot inputStreamWithNot, byte[] headerBytes, boolean verifyChecksum) throws IOException {
        DimHeader header = DimHeaderReader.dimHeaderFromBytes(headerBytes);
        inputStreamWithNot.readToOffset(0x30000); // skip everything until stats section
        byte[] bytes = inputStreamWithNot.readToOffset(0x40000);
        DimStats stats = DimStatsReader.dimStatsFromBytes(bytes);
        bytes = inputStreamWithNot.readToOffset(0x50000);
        DimEvolutionRequirements evolutionRequirements = DimEvolutionsReader.dimEvolutionRequirementsFromBytes(bytes);
        bytes = inputStreamWithNot.readToOffset(0x60000);
        DimAdventures adventures = DimAdventuresReader.dimAdventuresFromBytes(bytes);
        byte[] spriteDimensions = inputStreamWithNot.readToOffset(0x70000);
        bytes = inputStreamWithNot.readToOffset(0x80000);
        DimFusions fusions = DimFusionsReader.dimFusionsFromBytes(bytes);
        bytes = inputStreamWithNot.readToOffset(0x100000);
        DimSpecificFusions dimSpecificFusions = DimSpecificFusionsReader.dimSpecificFusionsFromBytes(bytes);
        SpriteData spriteData = DimSpritesReader.spriteDataFromBytesAndStream(spriteDimensions, inputStreamWithNot);
        inputStreamWithNot.readToOffset(0x3FFFFE);
        bytes = inputStreamWithNot.readNBytes(2);
        int dimChecksum = ByteUtils.getUnsigned16Bit(bytes)[0];
        int calculatedChecksum = inputStreamWithNot.getChecksum();
        if(dimChecksum != calculatedChecksum) {
            log.warn("Checksums don't match! Calculated: {} Received: {}", Integer.toHexString(calculatedChecksum), Integer.toHexString(dimChecksum));
            if(verifyChecksum) {
                throw new IllegalStateException("Invalid Dim. Calculated checksum doesn't match Dim checksum");
            }
        }
        return DimCard.builder()
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
    }
}
