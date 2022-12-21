package com.github.cfogrady.vb.dim.fusion;

import com.github.cfogrady.vb.dim.sprite.BemSpriteReader;
import com.github.cfogrady.vb.dim.sprite.BemSpriteWriter;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FusionReaderTest {
    private final Random random = new Random();
    private BemFusionReader bemFusionReader;
    private BemFusionWriter bemFusionWriter;

    @BeforeEach
    void setup() {
        this.bemFusionReader = new BemFusionReader();
        this.bemFusionWriter = new BemFusionWriter();
    }

    @Test
    void testReadAttributeFusion() {
        BemAttributeFusions expectedAttributeFusions = createAttributeFusions();
        byte[] table = getAttributeTableInBytes(expectedAttributeFusions);
        Assertions.assertEquals(BemFusionConstants.MAX_ATTRIBUTE_TABLE_SIZE*BemFusionConstants.ATTRIBUTE_TABLE_ROW_SIZE*2, table.length);
        BemAttributeFusions readAttributeFusions = bemFusionReader.readAttributeFusion(new RelativeByteOffsetInputStream(new ByteArrayInputStream(table)));
        Assertions.assertEquals(expectedAttributeFusions.getEntries().size(), readAttributeFusions.getEntries().size());
        for(int i = 0; i < readAttributeFusions.getEntries().size(); i++) {
            BemAttributeFusions.BemAttributeFusionEntry expected = expectedAttributeFusions.getEntries().get(i);
            BemAttributeFusions.BemAttributeFusionEntry read = readAttributeFusions.getEntries().get(i);
            Assertions.assertEquals(expected.getCharacterIndex(), read.getCharacterIndex());
            Assertions.assertEquals(expected.getAttribute3Fusion(), read.getAttribute3Fusion());
            Assertions.assertEquals(expected.getAttribute2Fusion(), read.getAttribute2Fusion());
            Assertions.assertEquals(expected.getAttribute1Fusion(), read.getAttribute1Fusion());
            Assertions.assertEquals(expected.getAttribute4Fusion(), read.getAttribute4Fusion());
        }
    }

    @Test
    void testReadSpecificFusion() {
        BemSpecificFusions expectedSpecificFusions = createSpecificFusions();
        byte[] table = getSpecificTableInBytes(expectedSpecificFusions);
        Assertions.assertEquals(BemFusionConstants.MAX_SPECIFIC_TABLE_SIZE*BemFusionConstants.SPECIFIC_TABLE_ROW_SIZE*2, table.length);
        BemSpecificFusions readSpecificFusions = bemFusionReader.readSpecificFusions(new RelativeByteOffsetInputStream(new ByteArrayInputStream(table)));
        Assertions.assertEquals(expectedSpecificFusions.getEntries().size(), readSpecificFusions.getEntries().size());
        for(int i = 0; i < readSpecificFusions.getEntries().size(); i++) {
            BemSpecificFusions.BemSpecificFusionEntry expected = expectedSpecificFusions.getEntries().get(i);
            BemSpecificFusions.BemSpecificFusionEntry read = readSpecificFusions.getEntries().get(i);
            Assertions.assertEquals(expected.getFromBemId(), read.getFromBemId());
            Assertions.assertEquals(expected.getFromCharacterIndex(), read.getFromCharacterIndex());
            Assertions.assertEquals(expected.getToBemId(), read.getToBemId());
            Assertions.assertEquals(expected.getToCharacterIndex(), read.getToCharacterIndex());
            Assertions.assertEquals(expected.getBackupBemId(), read.getBackupBemId());
            Assertions.assertEquals(expected.getBackupCharacterId(), read.getBackupCharacterId());
        }
    }

    private BemAttributeFusions createAttributeFusions() {
        List<BemAttributeFusions.BemAttributeFusionEntry> entries = new ArrayList<>(BemFusionConstants.MAX_ATTRIBUTE_TABLE_SIZE);
        for(int i = 0; i < BemFusionConstants.MAX_ATTRIBUTE_TABLE_SIZE; i++) {
            entries.add(BemAttributeFusions.BemAttributeFusionEntry.builder()
                    .characterIndex(random.nextInt(23))
                    .attribute3Fusion(random.nextInt(23))
                    .attribute2Fusion(random.nextInt(23))
                    .attribute1Fusion(random.nextInt(23))
                    .attribute4Fusion(random.nextInt(23))
                    .build());
        }
        return BemAttributeFusions.builder().entries(entries).build();
    }

    private BemSpecificFusions createSpecificFusions() {
        List<BemSpecificFusions.BemSpecificFusionEntry> entries = new ArrayList<>(BemFusionConstants.MAX_SPECIFIC_TABLE_SIZE);
        for(int i = 0; i < BemFusionConstants.MAX_SPECIFIC_TABLE_SIZE; i++) {
            entries.add(BemSpecificFusions.BemSpecificFusionEntry.builder()
                    .fromBemId(random.nextInt(64))
                    .fromCharacterIndex(random.nextInt(23))
                    .toBemId(random.nextInt(64))
                    .toCharacterIndex(random.nextInt(23))
                    .backupBemId(random.nextInt(64))
                    .backupCharacterId(random.nextInt(23))
                    .build());
        }
        return BemSpecificFusions.builder().entries(entries).build();
    }

    private byte[] getAttributeTableInBytes(BemAttributeFusions attributeFusions) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bemFusionWriter.writeAttributeFusions(attributeFusions, new RelativeByteOffsetOutputStream(outputStream));
        return outputStream.toByteArray();
    }

    private byte[] getSpecificTableInBytes(BemSpecificFusions specificFusions) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bemFusionWriter.writeSpecificFusions(specificFusions, new RelativeByteOffsetOutputStream(outputStream));
        return outputStream.toByteArray();
    }
}
