package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimFusions;
import com.github.cfogrady.vb.dim.reader.content.DimSpecificFusions;

import java.io.IOException;

public class SpecificFusionsWriter {
    public static void writeSpecificFusions(DimSpecificFusions specificFusions, OutputStreamWithNot outputStreamWithNot, boolean strictEmulation) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x80000);
        int currentIndex = 0;
        for(DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry : specificFusions.getDimSpecificFusionBlocks()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getStatsIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getStatsIndexForFusionResult()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getFusionDimId()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getFusionDimSlotId()));
            currentIndex++;
        }
        if(strictEmulation && currentIndex < DimSpecificFusions.VB_TABLE_SIZE) {
            for(int slot = currentIndex; slot < DimSpecificFusions.VB_TABLE_SIZE; slot++) {
                for(int i = 0; i < 4; i++) {
                    outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                }
            }
        }
    }
}
