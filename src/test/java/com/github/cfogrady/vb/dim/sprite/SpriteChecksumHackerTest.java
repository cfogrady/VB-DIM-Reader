package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.RawChecksumBuilder;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class SpriteChecksumHackerTest {

    @BeforeAll
    public static void setupSpriteWriter() {
        SpriteChecksumAreasCalculator spriteChecksumAreasCalculator = SpriteChecksumAreasCalculator.buildForBEM();
        UnorderedSpriteChecksumHacker unorderedSpriteChecksumHacker = new UnorderedSpriteChecksumHacker(spriteChecksumAreasCalculator, SpriteWriter.PIXEL_POINTER_TABLE_START, new RawChecksumBuilder());
        spriteWriter = new SpriteWriter(unorderedSpriteChecksumHacker);
    }

    private static SpriteWriter spriteWriter;
    private final static Random random = new Random();
    private final static BemSpriteReader bemSpriteReader = new BemSpriteReader();

    private SpriteData setupBEMRandomSpriteData(int characters) {
        String text = "Some text";
        return new SpriteData(setupBEMRandomSprites(characters), text, new ArrayList<>());
    }

    private List<SpriteData.Sprite> setupBEMRandomSprites(int characters) {
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        sprites.add(generateRandomSprite(42, 42)); // icon
        sprites.add(generateRandomSprite(80, 160)); // background 0
        for(int i = 0; i < 8; i++) {
            sprites.add(generateRandomSprite(32, 40)); //eggs
        }
        sprites.add(generateRandomSprite(80, 160)); // background 1
        sprites.add(generateRandomSprite(80, 17)); // Ready
        sprites.add(generateRandomSprite(80, 32)); // Go
        sprites.add(generateRandomSprite(80, 24)); // Win
        sprites.add(generateRandomSprite(80, 24)); // Lose
        for(int i = 0; i < 3; i++) {
            sprites.add(generateRandomSprite(61, 57)); // Hit Animation
        }
        for(int i = 0; i < 4; i++) {
            sprites.add(generateRandomSprite(30, 16)); // Attributes
        }
        for(int i = 0; i < 8; i++) {
            sprites.add(generateRandomSprite(78, 18)); // Stages
        }
        for(int i = 0; i < 4; i++) {
            sprites.add(generateRandomSprite(80, 160)); // Backgrounds
        }
        for(int i = 0; i < 10; i++) {
            sprites.add(generateRandomSprite(20, 20)); // Small Attacks
        }
        for(int i = 0; i < 10; i++) {
            sprites.add(generateRandomSprite(34, 44)); // Big Attacks
        }
        for (int i = 0; i < characters; i ++) {
            sprites.add(generateRandomSprite(160, 15)); // fake name text
            for(int j = 0; j < 12; j++) {
                sprites.add(generateRandomSprite(64, 56)); // fake sprites
            }
            sprites.add(generateRandomSprite(80, 160)); // fake cut-in
        }
        return sprites;
    }

    private byte[] writeSpriteDataToByteArray(SpriteData spriteData) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ByteOffsetOutputStream byteOffsetOutputStream = new RelativeByteOffsetOutputStream(baos);
            spriteWriter.writeUnmodified(spriteData, byteOffsetOutputStream);
            return baos.toByteArray();
        }
    }

    private byte[] writeSpriteDataToByteArrayUsingChecksumHack(SpriteData spriteData) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ByteOffsetOutputStream byteOffsetOutputStream = new RelativeByteOffsetOutputStream(baos);
            spriteWriter.writeSpriteDataToMatchChecksum(byteOffsetOutputStream, spriteData);
            return baos.toByteArray();
        }
    }

    private byte[] writeSpriteDimensionsToByteArray(SpriteData spriteData) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ByteOffsetOutputStream byteOffsetOutputStream = new RelativeByteOffsetOutputStream(baos);
            SpriteDimentionsWriter.writeSpriteDimensionsAtCurrentLocation(spriteData, byteOffsetOutputStream);
            // so the reader doesn't hang at the end of the array
            byteOffsetOutputStream.writeInt(0);
            return baos.toByteArray();
        }
    }

    private List<SpriteData.SpriteDimensions> getSpriteDimensionsFromBytes(byte[] spriteDimensionBytes) throws IOException {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(spriteDimensionBytes)) {
            RelativeByteOffsetInputStream byteOffsetInputStream = new RelativeByteOffsetInputStream(bais);
            return bemSpriteReader.readSpriteDimensions(byteOffsetInputStream);
        }
    }

    private SpriteData getSpriteDataFromBytes(byte[] spriteBytes, List<SpriteData.SpriteDimensions> spriteDimensions) throws IOException {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(spriteBytes)) {
            RelativeByteOffsetInputStream byteOffsetInputStream = new RelativeByteOffsetInputStream(bais);
            return bemSpriteReader.getSpriteData(byteOffsetInputStream, spriteDimensions);
        }
    }

    @Test
    void testThatSmallerSpritesMaintainsChecksums() throws IOException {
        SpriteData originalSpriteData = setupBEMRandomSpriteData(23); //full roster
        byte[] writtenOriginalSpriteData = writeSpriteDataToByteArray(originalSpriteData);
        byte[] writtenSpriteSizes = writeSpriteDimensionsToByteArray(originalSpriteData);
        List<SpriteData.SpriteDimensions> originalSpriteDimensions = getSpriteDimensionsFromBytes(writtenSpriteSizes);
        SpriteData originalSpriteDataWithChecksums = getSpriteDataFromBytes(writtenOriginalSpriteData, originalSpriteDimensions);

        SpriteData smallerSpriteData = new SpriteData(setupBEMRandomSprites(7), originalSpriteDataWithChecksums.getText(), originalSpriteDataWithChecksums.getSpriteChecksums());
        byte[] writtenNewSpriteDimensions = writeSpriteDimensionsToByteArray(smallerSpriteData);
        byte[] writtenNewSprites = writeSpriteDataToByteArrayUsingChecksumHack(smallerSpriteData);
        List<SpriteData.SpriteDimensions> readSmallerSpriteDimensions = getSpriteDimensionsFromBytes(writtenNewSpriteDimensions);
        SpriteData readSmallerSpriteData = getSpriteDataFromBytes(writtenNewSprites, readSmallerSpriteDimensions);
        log.info("Sprite checksums: {}", originalSpriteDataWithChecksums.getSpriteChecksums());
        log.info("New Sprite checksums: {}", readSmallerSpriteData.getSpriteChecksums());
        Assertions.assertArrayEquals(originalSpriteDataWithChecksums.getSpriteChecksums().toArray(new Integer[0]), readSmallerSpriteData.getSpriteChecksums().toArray(new Integer[0]));
    }



    SpriteData.Sprite generateRandomSprite(int width, int height) {
        byte[] pixelData = new byte[width * height * 2];
        random.nextBytes(pixelData);
        return new SpriteData.Sprite(width, height, pixelData);
    }

// TODO: Break DimSpriteReader and probably the writer down into smaller components before writing these tests.

//    @Test
//    void testThatSpritesStayWhenChecksumMatches() {
//        //fully encompass, and split between sprites, multiple split between sprites
//        Assertions.assertTrue(false, "Write the test");
//    }
//
//    @Test
//    void testThatEncompassingSpriteIsMovedToChecksumStartWhenChecksumMatchFails() {
//        Assertions.assertTrue(false, "Write the test");
//    }
//
//    @Test
//    void testThatSpritesSplitHasChecksumBetweenThem() {
//        Assertions.assertTrue(false, "Write the test");
//    }
//
//    @Test
//    void testThatMultipleSpritesSplitHaveChecksumBeforeLast() {
//        Assertions.assertTrue(false, "Write the test");
//    }
//
//    @Test
//    void testThatAWeaveMatchesChecksumsAfter() {
//        Assertions.assertTrue(false, "Write the test");
//    }
}
