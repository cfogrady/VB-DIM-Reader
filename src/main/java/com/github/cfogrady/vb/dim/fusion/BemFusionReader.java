package com.github.cfogrady.vb.dim.fusion;

import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BemFusionReader {
    public BemAttributeFusions readAttributeFusion(ByteOffsetInputStream generalInputStream) {
        try {
            RelativeByteOffsetInputStream relativeInputStream = new RelativeByteOffsetInputStream(generalInputStream);
            List<BemAttributeFusions.BemAttributeFusionEntry> attributeFusions = new ArrayList<>(BemFusionConstants.MAX_ATTRIBUTE_TABLE_SIZE);
            int[] values = getAttributeRowValues(relativeInputStream);
            boolean validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            while (validRow) {
                attributeFusions.add(BemAttributeFusions.BemAttributeFusionEntry.builder()
                        .characterIndex(values[0])
                        .attribute3Fusion(values[1])
                        .attribute2Fusion(values[2])
                        .attribute1Fusion(values[3])
                        .attribute4Fusion(values[4])
                        .build());
                values = getAttributeRowValues(relativeInputStream);
                validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            }
            return BemAttributeFusions.builder().entries(attributeFusions).build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private int[] getAttributeRowValues(ByteOffsetInputStream inputStream) throws IOException {
        byte[] rowBytes = inputStream.readNBytes(BemFusionConstants.ATTRIBUTE_TABLE_ROW_SIZE * 2);
        return ByteUtils.getUnsigned16Bit(rowBytes);
    }

    public BemSpecificFusions readSpecificFusions(ByteOffsetInputStream generalInputStream) {
        try {
            RelativeByteOffsetInputStream relativeInputStream = new RelativeByteOffsetInputStream(generalInputStream);
            List<BemSpecificFusions.BemSpecificFusionEntry> specificFusionEntries = new ArrayList<>(BemFusionConstants.MAX_SPECIFIC_TABLE_SIZE);
            int[] values = getSpecificFusionRowValues(relativeInputStream);
            boolean validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            while (validRow) {
                specificFusionEntries.add(BemSpecificFusions.BemSpecificFusionEntry.builder()
                                .fromBemId(values[0])
                                .fromCharacterIndex(values[1])
                                .toBemId(values[2])
                                .toCharacterIndex(values[3])
                                .backupBemId(values[4])
                                .backupCharacterId(values[5])
                                .build());
                values = getSpecificFusionRowValues(relativeInputStream);
                validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            }
            return BemSpecificFusions.builder().entries(specificFusionEntries).build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private int[] getSpecificFusionRowValues(ByteOffsetInputStream inputStream) throws IOException {
        byte[] rowBytes = inputStream.readNBytes(BemFusionConstants.SPECIFIC_TABLE_ROW_SIZE * 2);
        return ByteUtils.getUnsigned16Bit(rowBytes);
    }
}
