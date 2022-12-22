package com.github.cfogrady.vb.dim.card;

import com.github.cfogrady.vb.dim.adventure.BemAdventureWriter;
import com.github.cfogrady.vb.dim.character.BemCharacterWriter;
import com.github.cfogrady.vb.dim.fusion.BemFusionWriter;
import com.github.cfogrady.vb.dim.header.BemHeaderWriter;
import com.github.cfogrady.vb.dim.sprite.BemSpriteWriter;
import com.github.cfogrady.vb.dim.transformation.BemTransformationWriter;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.DIMChecksumBuilder;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

@RequiredArgsConstructor
@Slf4j
public class BemCardWriter {
    private final BemHeaderWriter bemHeaderWriter;
    private final BemCharacterWriter bemCharacterWriter;
    private final BemTransformationWriter bemTransformationWriter;
    private final BemAdventureWriter bemAdventureWriter;
    private final BemSpriteWriter bemSpriteWriter;
    private final BemFusionWriter bemFusionWriter;

    public BemCardWriter() {
        this.bemHeaderWriter = new BemHeaderWriter();
        this.bemCharacterWriter = new BemCharacterWriter();
        this.bemTransformationWriter = new BemTransformationWriter();
        this.bemAdventureWriter = new BemAdventureWriter();
        this.bemSpriteWriter = new BemSpriteWriter();
        this.bemFusionWriter = new BemFusionWriter();
    }

    public void writeBemCard(BemCard bemCard, OutputStream outputStream) {
        DIMChecksumBuilder checksumBuilder = new DIMChecksumBuilder();
        OutputStreamWithNot outputStreamWithNot = OutputStreamWithNot.wrap(outputStream, checksumBuilder);
        try {
            bemHeaderWriter.writeHeader(bemCard.getBemHeader(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(0x10000);
            outputStreamWithNot.write16BitValueUntilOffset(BemCardConstants.NONE_VALUE, 0x20000);
            outputStreamWithNot.writeZerosUntilOffset(BemCardConstants.CHARACTER_SECTION_START);
            bemCharacterWriter.writeBemCharacters(bemCard.getBemCharacterStats(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(BemCardConstants.TRANSFORMATION_SECTION_START);
            bemTransformationWriter.writeBemTransformations(bemCard.getBemTransformationRequirements(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(BemCardConstants.ADVENTURE_SECTION_START);
            bemAdventureWriter.writeAdventures(bemCard.getBemAdventureLevels(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(BemCardConstants.SPRITE_DIMENSIONS_START);
            bemSpriteWriter.writeSpriteDimensions(bemCard.getSpriteData(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(BemCardConstants.ATTRIBUTE_FUSION_START);
            bemFusionWriter.writeAttributeFusions(bemCard.getBemAttributeFusions(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(BemCardConstants.SPECIFIC_FUSION_START);
            bemFusionWriter.writeSpecificFusions(bemCard.getBemSpecificFusions(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(0x90000);
            outputStreamWithNot.write16BitValueUntilOffset(BemCardConstants.NONE_VALUE, BemCardConstants.SPRITE_PACKAGE_START);
            bemSpriteWriter.writeSpritePackage(bemCard.getSpriteData(), bemCard.getBemHeader().hasSpriteSignature(), outputStreamWithNot);
            outputStreamWithNot.writeZerosUntilOffset(BemCardConstants.CHECKSUM_LOCATION);
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(outputStreamWithNot.getChecksum()));
        } catch (IOException e) {
            log.error("Failed to write DIM file!", e);
            throw new UncheckedIOException(e);
        }
    }
}
