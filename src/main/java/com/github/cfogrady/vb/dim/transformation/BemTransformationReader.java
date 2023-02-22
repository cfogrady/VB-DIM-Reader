package com.github.cfogrady.vb.dim.transformation;

import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class BemTransformationReader {
    public BemTransformationRequirements readTransformations(ByteOffsetInputStream generalInputStream) {
        RelativeByteOffsetInputStream relativeInputStream = new RelativeByteOffsetInputStream(generalInputStream);
        List<BemTransformationRequirements.BemTransformationRequirementEntry> entries = new ArrayList<>(BemTransformationConstants.MAX_TABLE_SIZE);
        try {
            int[] values = getRowValues(relativeInputStream);
            boolean validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            while (validRow) {
                entries.add(BemTransformationRequirements.BemTransformationRequirementEntry.builder()
                                .fromCharacterIndex(values[0])
                                .minutesUntilTransformation(values[1])
                                .requiredVitalValues(values[2])
                                .requiredTrophies(values[3])
                                .requiredBattles(values[4])
                                .requiredWinRatio(values[5])
                                .minimumMinuteOfHour(values[6])
                                .maximumMinuteOfHour(values[7])
                                .requiredCompletedAdventureLevel(values[8])
                                .toCharacterIndex(values[9])
                                .isNotSecret(values[10])
                        .build());
                values = getRowValues(relativeInputStream);
                validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            }
            return BemTransformationRequirements.builder().transformationEntries(entries).build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private int[] getRowValues(RelativeByteOffsetInputStream relativeInputStream) throws IOException {
        byte[] rowBytes = relativeInputStream.readNBytes(BemTransformationConstants.ROW_SIZE * 2);
        return ByteUtils.getUnsigned16Bit(rowBytes);
    }
}
