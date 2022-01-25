package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimAdventures;

import java.io.IOException;

public class AdventuresWriter {
    public static void writeAdventures(DimAdventures dimAdventures, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x50000);
        for(DimAdventures.DimAdventureBlock adventureEntry : dimAdventures.getAdventureBlocks()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getSteps()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossStatsIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossDp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossHp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossAp()));
        }
    }
}
