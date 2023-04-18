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
            log.info("Read Header Data: {}" + System.currentTimeMillis());
            byte[] headerBytes = inputStreamWithNot.readNBytes(0x1030);
            return readBemCard(headerBytes, inputStreamWithNot);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

     BemCard readBemCard(byte[] headerBytes, InputStreamWithNot inputStream) {
        log.info("Parsing header from bytes at: {}", System.currentTimeMillis());
        BemHeader header = bemHeaderReader.readBemHeaderFromHeaderBytes(headerBytes);
         log.info("Header Read At: {}. Read to character section", System.currentTimeMillis());
        try {
            inputStream.readToOffset(BemCardConstants.CHARACTER_SECTION_START);
            log.info("Ready for character section: {}. Reading...", System.currentTimeMillis());
            BemCharacterStats characters = bemCharacterReader.readCharacterStats(inputStream);
            log.info("Character section loaded: {}. Skip to transformation", System.currentTimeMillis());
            inputStream.readToOffset(BemCardConstants.TRANSFORMATION_SECTION_START);
            log.info("Ready for transformation section: {}. Reading...", System.currentTimeMillis());
            BemTransformationRequirements transformations = bemTransformationReader.readTransformations(inputStream);
            log.info("Transformations loaded: {}. Skipping to adventure", System.currentTimeMillis());
            inputStream.readToOffset(BemCardConstants.ADVENTURE_SECTION_START);
            log.info("Ready for adventures: {}. Reading...", System.currentTimeMillis());
            BemAdventureLevels adventureLevels = bemAdventuresReader.readAdventures(inputStream);
            log.info("Adventures loaded: {}. Skipping to sprite dimensions", System.currentTimeMillis());
            inputStream.readToOffset(BemCardConstants.SPRITE_DIMENSIONS_START);
            log.info("Ready for sprite dimensions: {}. Reading", System.currentTimeMillis());
            List<SpriteData.SpriteDimensions> spriteDimensions = bemSpriteReader.readSpriteDimensions(inputStream);
            log.info("Sprite dimensions loaded: {}. Skipping to attribute fusions", System.currentTimeMillis());
            inputStream.readToOffset(BemCardConstants.ATTRIBUTE_FUSION_START);
            log.info("Ready for attribute fusions: {}. Reading", System.currentTimeMillis());
            AttributeFusions attributeFusions = bemFusionReader.readAttributeFusion(inputStream);
            log.info("Attribute fusions loaded: {}. Skipping to specific fusion", System.currentTimeMillis());
            inputStream.readToOffset(BemCardConstants.SPECIFIC_FUSION_START);
            log.info("Ready for specific fusion: {}. Reading", System.currentTimeMillis());
            BemSpecificFusions specificFusions = bemFusionReader.readSpecificFusions(inputStream);
            log.info("Specific Fusions loaded: {}. Skipping to sprite data", System.currentTimeMillis());
            inputStream.readToOffset(BemCardConstants.SPRITE_PACKAGE_START);
            log.info("Ready for Sprite Data: {}. Reading", System.currentTimeMillis());
            SpriteData spriteData = bemSpriteReader.getSpriteData(inputStream, spriteDimensions);
            log.info("Sprite data loaded: {}. Read to the checksum", System.currentTimeMillis());
            int checksumOnCard = readToChecksum(inputStream);
            log.info("Checksum read: {}. Building card.", System.currentTimeMillis());
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
