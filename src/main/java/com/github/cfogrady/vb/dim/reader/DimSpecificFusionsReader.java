package com.github.cfogrady.vb.dim.reader;

import com.github.cfogrady.vb.dim.reader.content.DimSpecificFusions;

import java.util.ArrayList;
import java.util.List;

class DimSpecificFusionsReader {
    static DimSpecificFusions dimSpecificFusionsFromBytes(byte[] bytes, Integer maxDimSpecificFusions) {
        List<DimSpecificFusions.DimSpecificFusionBlock> dimSpecificFusionBlocks = new ArrayList<>();
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        boolean onlyZeroRow = false;
        int indexLimit = maxDimSpecificFusions != null ? maxDimSpecificFusions*4 : values.length;
        for(int index = 0; index < indexLimit && !onlyZeroRow; index+=4) {
            onlyZeroRow = ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 4);
            if(!onlyZeroRow) {
                DimSpecificFusions.DimSpecificFusionBlock block = DimSpecificFusions.DimSpecificFusionBlock.builder()
                        .statsIndex(values[index])
                        .statsIndexForFusionResult(values[index+1])
                        .fusionDimId(values[index+2])
                        .fusionDimSlotId(values[index+3])
                        .build();
                dimSpecificFusionBlocks.add(block);
            }
        }
        return DimSpecificFusions.builder().dimSpecificFusionBlocks(dimSpecificFusionBlocks).build();
    }
}
