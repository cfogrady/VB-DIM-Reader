package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimFusions;

import java.io.IOException;

public class FusionsWriter {
    public static void writeFusions(DimFusions dimFusions, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x70000);
        for(DimFusions.DimFusionBlock fusionEntry : dimFusions.getFusionBlocks()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getStatsIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getStatsIndexForFusionWithType3()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getStatsIndexForFusionWithType2()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getStatsIndexForFusionWithType1()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getStatsIndexForFusionWithType4()));
        }
    }
}
