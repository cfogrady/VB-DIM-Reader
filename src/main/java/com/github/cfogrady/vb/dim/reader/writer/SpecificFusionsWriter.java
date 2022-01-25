package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimSpecificFusions;

import java.io.IOException;

public class SpecificFusionsWriter {
    public static void writeSpecificFusions(DimSpecificFusions specificFusions, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x80000);
        for(DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry : specificFusions.getDimSpecificFusionBlocks()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getStatsIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getStatsIndexForFusionResult()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getFusionDimId()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getFusionDimSlotId()));
        }
    }
}
