package com.github.cfogrady.vb.dim.card;


import com.github.cfogrady.vb.dim.adventure.AdventuresWriter;
import com.github.cfogrady.vb.dim.character.StatsWriter;
import com.github.cfogrady.vb.dim.fusion.FusionsWriter;
import com.github.cfogrady.vb.dim.fusion.SpecificFusionsWriter;
import com.github.cfogrady.vb.dim.header.HeaderWriter;
import com.github.cfogrady.vb.dim.sprite.*;
import com.github.cfogrady.vb.dim.transformation.EvolutionsWriter;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.DIMChecksumBuilder;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;
import com.github.cfogrady.vb.dim.util.RawChecksumBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

@Slf4j
@RequiredArgsConstructor
public class DimWriter {
    private final BemCardWriter bemCardWriter;
    private final SpriteWriter spriteWriter;

    public DimWriter() {
        SpriteChecksumAreasCalculator spriteChecksumAreasCalculator = SpriteChecksumAreasCalculator.buildForDIM();
        UnorderedSpriteChecksumHacker unorderedChecksumHacker = new UnorderedSpriteChecksumHacker(spriteChecksumAreasCalculator, SpriteWriter.PIXEL_POINTER_TABLE_START, new RawChecksumBuilder());
        spriteWriter = new SpriteWriter(unorderedChecksumHacker);
        bemCardWriter = new BemCardWriter();
    }

    public static final int NONE_VALUE = DimReader.NONE_VALUE;

    public void writeDimData(DimCard dimContent, OutputStream outputStream) {
        DIMChecksumBuilder checksumBuilder = new DIMChecksumBuilder();
        OutputStreamWithNot outputStreamWithNot = OutputStreamWithNot.wrap(outputStream, checksumBuilder);
        try {
            HeaderWriter.writeHeader(dimContent.getHeader(), outputStreamWithNot);
            StatsWriter.writeStats(dimContent.getCharacterStats(), outputStreamWithNot);
            EvolutionsWriter.writeEvolutions(dimContent.getTransformationRequirements(), outputStreamWithNot);
            AdventuresWriter.writeAdventures(dimContent.getAdventureLevels(), outputStreamWithNot);
            SpriteDimentionsWriter.writeSpriteDimensions(dimContent.getSpriteData(), outputStreamWithNot);
            FusionsWriter.writeFusions(dimContent.getAttributeFusions(), outputStreamWithNot);
            SpecificFusionsWriter.writeSpecificFusions(dimContent.getSpecificFusions(), outputStreamWithNot);
            spriteWriter.writeSpriteData(dimContent.getSpriteData(), dimContent.getHeader().hasSpriteSignature(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(0x3ffffe);
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(outputStreamWithNot.getChecksum()));
        } catch (IOException e) {
            log.error("Failed to write DIM file!", e);
            throw new UncheckedIOException(e);
        }
    }
    
    public void writeCard(Card card, OutputStream outputStream) {
        if(card instanceof DimCard) {
            writeDimData((DimCard) card, outputStream);
        } else if(card instanceof BemCard) {
            bemCardWriter.writeBemCard((BemCard) card, outputStream);
        } else {
            throw new IllegalArgumentException("Unrecognized card type: " + card.getClass().getName());
        }
    }
}
