package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimFusions;

import java.util.ArrayList;
import java.util.List;

class DimFusionsReader {
    static DimFusions dimFusionsFromBytes(byte[] bytes) {
        List<DimFusions.DimFusionBlock> fusionBlocks = new ArrayList<>();
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        boolean onlyZeroRow = false;
        for(int index = 0; index < values.length && !onlyZeroRow; index+=5) {
            onlyZeroRow = ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 5);
            if(!onlyZeroRow) {
                DimFusions.DimFusionBlock block = DimFusions.DimFusionBlock.builder()
                        .statsIndex(values[index])
                        .statsIndexForFusionWithType3(values[index+1])
                        .statsIndexForFusionWithType2(values[index+2])
                        .statsIndexForFusionWithType1(values[index+3])
                        .statsIndexForFusionWithType4(values[index+4])
                        .build();
                fusionBlocks.add(block);
            }
        }
        return DimFusions.builder().fusionBlocks(fusionBlocks).build();
    }
}
