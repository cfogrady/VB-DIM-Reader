package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimSpecificFusions;

import java.util.ArrayList;
import java.util.List;

class DimSpecificFusionsReader {
    static DimSpecificFusions dimSpecificFusionsFromBytes(byte[] bytes) {
        List<DimSpecificFusions.DimSpecificFusionBlock> dimSpecificFusionBlocks = new ArrayList<>(DimSpecificFusions.VB_TABLE_SIZE);
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        int index = 0;
        boolean onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 4);
        int dummyRows = 0;
        while(!onlyZeroRow) {
            if (!ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 4)) {
                DimSpecificFusions.DimSpecificFusionBlock block = DimSpecificFusions.DimSpecificFusionBlock.builder()
                        .statsIndex(values[index])
                        .statsIndexForFusionResult(values[index+1])
                        .fusionDimId(values[index+2])
                        .fusionDimSlotId(values[index+3])
                        .build();
                dimSpecificFusionBlocks.add(block);
            } else {
                dummyRows++;
            }
            index += 4;
            onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 4); //find out if the next row is only zeros
        }
        return DimSpecificFusions.builder().dimSpecificFusionBlocks(dimSpecificFusionBlocks).dummyRows(dummyRows).build();
    }
}
