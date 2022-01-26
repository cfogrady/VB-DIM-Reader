package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimAdventures;

import java.io.IOException;

public class AdventuresWriter {
    public static void writeAdventures(DimAdventures dimAdventures, OutputStreamWithNot outputStreamWithNot, boolean strictEmulation) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x50000);
        int currentIndex = 0;
        for(DimAdventures.DimAdventureBlock adventureEntry : dimAdventures.getAdventureBlocks()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getSteps()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossStatsIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossDp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossHp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossAp()));
            currentIndex++;
        }
        if(strictEmulation && currentIndex < DimAdventures.VB_TABLE_SIZE) {
            for(int slot = currentIndex; slot < DimAdventures.VB_TABLE_SIZE; slot++) {
                for(int i = 0; i < 5; i++) {
                    outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                }
            }
        }
    }
}
