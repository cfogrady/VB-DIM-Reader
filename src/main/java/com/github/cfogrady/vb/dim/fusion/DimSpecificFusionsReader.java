package com.github.cfogrady.vb.dim.fusion;

import com.github.cfogrady.vb.dim.util.ByteUtils;

import java.util.ArrayList;
import java.util.List;

public class DimSpecificFusionsReader {
    public static DimSpecificFusions dimSpecificFusionsFromBytes(byte[] bytes) {
        List<SpecificFusions.SpecificFusionEntry> dimSpecificFusionBlocks = new ArrayList<>(DimSpecificFusions.VB_TABLE_SIZE);
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        int index = 0;
        boolean onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 4);
        int dummyRows = 0;
        while(!onlyZeroRow) {
            if (!ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 4)) {
                SpecificFusions.SpecificFusionEntry block = SpecificFusions.SpecificFusionEntry.builder()
                        .fromCharacterIndex(values[index])
                        .toCharacterIndex(values[index+1])
                        .backupDimId(values[index+2])
                        .backupCharacterIndex(values[index+3])
                        .build();
                dimSpecificFusionBlocks.add(block);
            } else {
                dummyRows++;
            }
            index += 4;
            onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 4); //find out if the next row is only zeros
        }
        return DimSpecificFusions.builder().entries(dimSpecificFusionBlocks).dummyRows(dummyRows).build();
    }
}
