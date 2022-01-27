package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimFusions;
import com.github.cfogrady.vb.dim.reader.content.DimStats;

import java.util.ArrayList;
import java.util.List;

class DimFusionsReader {
    static DimFusions dimFusionsFromBytes(byte[] bytes) {
        List<DimFusions.DimFusionBlock> fusionBlocks = new ArrayList<>(DimFusions.VB_TABLE_SIZE);
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        int index = 0;
        boolean onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5);
        int dummyRows = 0;
        while(!onlyZeroRow) {
            if (!ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 5)) {
                DimFusions.DimFusionBlock block = DimFusions.DimFusionBlock.builder()
                        .statsIndex(values[index])
                        .statsIndexForFusionWithType3(values[index+1])
                        .statsIndexForFusionWithType2(values[index+2])
                        .statsIndexForFusionWithType1(values[index+3])
                        .statsIndexForFusionWithType4(values[index+4])
                        .build();
                fusionBlocks.add(block);
            } else {
                dummyRows++;
            }
            index += 5;
            onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5); //find out if the next row is only zeros
        }
        return DimFusions.builder().fusionBlocks(fusionBlocks).dummyRows(dummyRows).build();
    }
}
