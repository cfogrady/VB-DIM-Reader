package com.github.cfogrady.vb.dim.card;


import com.github.cfogrady.vb.dim.adventure.BemAdventureLevels;
import com.github.cfogrady.vb.dim.adventure.BemAdventuresReader;
import com.github.cfogrady.vb.dim.character.BemCharacterReader;
import com.github.cfogrady.vb.dim.character.BemCharacterStats;
import com.github.cfogrady.vb.dim.fusion.AttributeFusions;
import com.github.cfogrady.vb.dim.fusion.BemFusionReader;
import com.github.cfogrady.vb.dim.fusion.BemSpecificFusions;
import com.github.cfogrady.vb.dim.header.BemHeader;
import com.github.cfogrady.vb.dim.header.BemHeaderReader;
import com.github.cfogrady.vb.dim.sprite.BemSpriteReader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.BemTransformationReader;
import com.github.cfogrady.vb.dim.transformation.BemTransformationRequirements;
import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.DIMChecksumBuilder;
import com.github.cfogrady.vb.dim.util.InputStreamWithNot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class BemCardReader {
    private final BemHeaderReader bemHeaderReader;
    private final BemCharacterReader bemCharacterReader;
    private final BemTransformationReader bemTransformationReader;
    private final BemAdventuresReader bemAdventuresReader;
    private final BemFusionReader bemFusionReader;
    private final BemSpriteReader bemSpriteReader;

    public BemCardReader() {
        bemHeaderReader = new BemHeaderReader();
        bemCharacterReader = new BemCharacterReader();
        bemTransformationReader = new BemTransformationReader();
        bemAdventuresReader = new BemAdventuresReader();
        bemFusionReader = new BemFusionReader();
        bemSpriteReader = new BemSpriteReader();
    }

    public BemCard readBemCard(InputStream inputStream) {
        DIMChecksumBuilder checksumBuilder = new DIMChecksumBuilder();
        InputStreamWithNot inputStreamWithNot = new InputStreamWithNot(inputStream, checksumBuilder);
        try {
            byte[] headerBytes = inputStreamWithNot.readNBytes(0x1030);
            return readBemCard(headerBytes, inputStreamWithNot);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

     BemCard readBemCard(byte[] headerBytes, InputStreamWithNot inputStream) {
        BemHeader header = bemHeaderReader.readBemHeaderFromHeaderBytes(headerBytes);
        try {
            inputStream.readToOffset(BemCardConstants.CHARACTER_SECTION_START);
            BemCharacterStats characters = bemCharacterReader.readCharacterStats(inputStream);
            inputStream.readToOffset(BemCardConstants.TRANSFORMATION_SECTION_START);
            BemTransformationRequirements transformations = bemTransformationReader.readTransformations(inputStream);
            inputStream.readToOffset(BemCardConstants.ADVENTURE_SECTION_START);
            BemAdventureLevels adventureLevels = bemAdventuresReader.readAdventures(inputStream);
            inputStream.readToOffset(BemCardConstants.SPRITE_DIMENSIONS_START);
            List<SpriteData.SpriteDimensions> spriteDimensions = bemSpriteReader.readSpriteDimensions(inputStream);
            inputStream.readToOffset(BemCardConstants.ATTRIBUTE_FUSION_START);
            AttributeFusions attributeFusions = bemFusionReader.readAttributeFusion(inputStream);
            inputStream.readToOffset(BemCardConstants.SPECIFIC_FUSION_START);
            BemSpecificFusions specificFusions = bemFusionReader.readSpecificFusions(inputStream);
            inputStream.readToOffset(BemCardConstants.SPRITE_PACKAGE_START);
            SpriteData spriteData = bemSpriteReader.getSpriteData(inputStream, spriteDimensions);
            int checksumOnCard = readToChecksum(inputStream);
            int calculatedChecksum = inputStream.getChecksum();
            if(checksumOnCard != calculatedChecksum) {
                log.warn("Checksums don't match! Calculated: {} Received: {}", Integer.toHexString(calculatedChecksum), Integer.toHexString(checksumOnCard));
            }

            return BemCard.builder()
                    .header(header)
                    .characterStats(characters)
                    .transformationRequirements(transformations)
                    .adventureLevels(adventureLevels)
                    .attributeFusions(attributeFusions)
                    .specificFusions(specificFusions)
                    .spriteData(spriteData)
                    .calculatedCheckSum(calculatedChecksum)
                    .checksum(checksumOnCard)
                    .build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private int readToChecksum(ByteOffsetInputStream inputStream) throws IOException {
        inputStream.readToOffset(BemCardConstants.CHECKSUM_LOCATION);
        byte[] checksumBytes = inputStream.readNBytes(2);
        int dimChecksum = ByteUtils.getUnsigned16Bit(checksumBytes)[0];
        return dimChecksum;
    }
}
