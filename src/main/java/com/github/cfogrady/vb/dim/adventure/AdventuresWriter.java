package com.github.cfogrady.vb.dim.adventure;

import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class AdventuresWriter {
    public static void writeAdventures(DimAdventures dimAdventures, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x50000);
        if(dimAdventures.getLevels().size() != DimAdventures.VB_TABLE_SIZE) {
            log.warn("Unexpected number of adventure missions... Might not work.");
        }
        int currentIndex = 0;
        for(DimAdventures.AdventureLevel adventureEntry : dimAdventures.getLevels()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getSteps()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossCharacterIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossDp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossHp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(adventureEntry.getBossAp()));
            currentIndex++;
        }
        if(dimAdventures.getDummyRows() > 0 && currentIndex < DimAdventures.VB_TABLE_SIZE) {
            for (int index = 0; index < dimAdventures.getDummyRows() && currentIndex + index < DimAdventures.VB_TABLE_SIZE; index++) {
                for(int i = 0; i < 5; i++) {
                    outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                }
            }
        }
    }
}
