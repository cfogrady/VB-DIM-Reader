package com.github.cfogrady.vb.dim.fusion;

import com.github.cfogrady.vb.dim.util.ByteUtils;

import java.util.ArrayList;
import java.util.List;

public class DimFusionsReader {
    public static DimFusions dimFusionsFromBytes(byte[] bytes) {
        List<DimFusions.AttributeFusionEntry> fusionBlocks = new ArrayList<>(DimFusions.VB_TABLE_SIZE);
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        int index = 0;
        boolean onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5);
        int dummyRows = 0;
        while(!onlyZeroRow) {
            if (!ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 5)) {
                DimFusions.AttributeFusionEntry block = DimFusions.AttributeFusionEntry.builder()
                        .characterIndex(values[index])
                        .attribute3Fusion(values[index+1])
                        .attribute2Fusion(values[index+2])
                        .attribute1Fusion(values[index+3])
                        .attribute4Fusion(values[index+4])
                        .build();
                fusionBlocks.add(block);
            } else {
                dummyRows++;
            }
            index += 5;
            onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5); //find out if the next row is only zeros
        }
        return DimFusions.builder().entries(fusionBlocks).dummyRows(dummyRows).build();
    }
}
