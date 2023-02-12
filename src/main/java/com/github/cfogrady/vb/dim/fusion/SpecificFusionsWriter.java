package com.github.cfogrady.vb.dim.fusion;

import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SpecificFusionsWriter {
    public static void writeSpecificFusions(DimSpecificFusions specificFusions, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x80000);
        int currentIndex = 0;
        for(SpecificFusions.SpecificFusionEntry specificFusionEntry : specificFusions.getEntries()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getFromCharacterIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getToCharacterIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getBackupDimId()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(specificFusionEntry.getBackupCharacterIndex()));
            currentIndex++;
        }
        if(specificFusions.getEntries().size() > 1) {
            log.warn("More than one specific fusion may not work...");
        }
        if(specificFusions.getDummyRows() > 0 && currentIndex < DimSpecificFusions.VB_TABLE_SIZE) {
            for (int index = 0; index < specificFusions.getDummyRows() && currentIndex + index < DimSpecificFusions.VB_TABLE_SIZE; index++) {
                for(int i = 0; i < 4; i++) {
                    outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                }
            }
        }
    }
}
