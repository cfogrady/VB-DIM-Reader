package com.github.cfogrady.vb.dim.transformation;

import com.github.cfogrady.vb.dim.card.BemCardConstants;
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

public class BemTransformationReaderTest {
    private final Random random = new Random();
    private BemTransformationReader bemTransformationReader;
    private BemTransformationWriter bemTransformationWriter;

    @BeforeEach
    void setup() {
        this.bemTransformationReader = new BemTransformationReader();
        this.bemTransformationWriter = new BemTransformationWriter();
    }

    @Test
    void testBemTransformationValuesReadCorrectly() {
        BemTransformationRequirements expectedTransformationRequirements = createBemTransformationRequirements(14);
        byte[] table = getTableInBytes(expectedTransformationRequirements);
        Assertions.assertEquals(BemTransformationConstants.MAX_TABLE_SIZE*BemTransformationConstants.ROW_SIZE*2, table.length);
        BemTransformationRequirements readRequirements = bemTransformationReader.readTransformations(new RelativeByteOffsetInputStream(new ByteArrayInputStream(table)));
        Assertions.assertEquals(expectedTransformationRequirements.getTransformationEntries().size(), readRequirements.getTransformationEntries().size());
        for(int i = 0; i < readRequirements.getTransformationEntries().size(); i++) {
            BemTransformationRequirements.BemTransformationRequirementEntry expected = expectedTransformationRequirements.getTransformationEntries().get(i);
            BemTransformationRequirements.BemTransformationRequirementEntry read = readRequirements.getTransformationEntries().get(i);
            Assertions.assertEquals(expected.getFromCharacterIndex(), read.getFromCharacterIndex());
            Assertions.assertEquals(expected.getMinutesUntilTransformation(), read.getMinutesUntilTransformation());
            Assertions.assertEquals(expected.getRequiredVitalValues(), read.getRequiredVitalValues());
            Assertions.assertEquals(expected.getRequiredPp(), read.getRequiredPp());
            Assertions.assertEquals(expected.getRequiredBattles(), read.getRequiredBattles());
            Assertions.assertEquals(expected.getRequiredWinRatio(), read.getRequiredWinRatio());
            Assertions.assertEquals(expected.getMinimumMinuteOfHour(), read.getMinimumMinuteOfHour());
            Assertions.assertEquals(expected.getMaximumMinuteOfHour(), read.getMaximumMinuteOfHour());
            Assertions.assertEquals(expected.getRequiredCompletedAdventureLevel(), read.getRequiredCompletedAdventureLevel());
            Assertions.assertEquals(expected.getToCharacterIndex(), read.getToCharacterIndex());
            Assertions.assertEquals(expected.getIsNotSecret(), read.getIsNotSecret());
        }
    }

    private BemTransformationRequirements createBemTransformationRequirements(int number) {
        List<BemTransformationRequirements.BemTransformationRequirementEntry> entries = new ArrayList<>(number);
        for(int i = 0; i < number; i++) {
            entries.add(BemTransformationRequirements.BemTransformationRequirementEntry.builder()
                    .fromCharacterIndex(random.nextInt(23))
                    .minutesUntilTransformation(random.nextInt(1441))
                    .requiredVitalValues(random.nextInt(4501))
                    .requiredPp(random.nextInt(21))
                    .requiredBattles(random.nextInt(21))
                    .requiredWinRatio(random.nextInt(76))
                    .minimumMinuteOfHour(random.nextInt(30))
                    .maximumMinuteOfHour(random.nextInt(BemCardConstants.NONE_VALUE))
                    .requiredCompletedAdventureLevel(random.nextInt(12))
                    .toCharacterIndex(random.nextInt(23))
                    .isNotSecret(random.nextInt(2))
                    .build());
        }
        return BemTransformationRequirements.builder().transformationEntries(entries).build();
    }

    private byte[] getTableInBytes(BemTransformationRequirements bemTransformationRequirements) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bemTransformationWriter.writeBemTransformations(bemTransformationRequirements, new RelativeByteOffsetOutputStream(outputStream));
        return outputStream.toByteArray();
    }
}
