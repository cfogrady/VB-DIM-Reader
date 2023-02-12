package com.github.cfogrady.vb.dim.fusion;

import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;

import java.io.IOException;

public class FusionsWriter {
    public static void writeFusions(DimFusions dimFusions, OutputStreamWithNot outputStreamWithNot) throws IOException {
        int currentIndex = 0;
        outputStreamWithNot.writeZerosUntilOffset(0x70000);
        for(DimFusions.AttributeFusionEntry fusionEntry : dimFusions.getEntries()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getCharacterIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getAttribute3Fusion()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getAttribute2Fusion()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getAttribute1Fusion()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(fusionEntry.getAttribute4Fusion()));
            currentIndex++;
        }
        if(dimFusions.getDummyRows() > 0 && currentIndex < DimFusions.VB_TABLE_SIZE) {
            for (int index = 0; index < dimFusions.getDummyRows() && currentIndex + index < DimFusions.VB_TABLE_SIZE; index++) {
                for(int i = 0; i < 5; i++) {
                    outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                }
            }
        }
    }
}
