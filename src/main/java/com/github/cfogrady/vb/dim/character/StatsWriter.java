package com.github.cfogrady.vb.dim.character;

import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;

import java.io.IOException;

public class StatsWriter {
    public static void writeStats(DimStats stats, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x30000);
        int currentSlot = 0;
        for(DimStats.DimStatBlock statsBlock : stats.getStatBlocks()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getStage()));
            if(statsBlock.isUnlockRequired()) {
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(1));
            } else {
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
            }
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getAttribute()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getDisposition()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getSmallAttackId()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getBigAttackId()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getDpStars()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getDp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getHp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getAp()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getFirstPoolBattleChance()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(statsBlock.getSecondPoolBattleChance()));
            currentSlot++;
        }
        if(stats.getDummyRows() > 0 && currentSlot < DimStats.VB_TABLE_SIZE) {
            for(int index = 0; index < stats.getDummyRows() && currentSlot + index < DimStats.VB_TABLE_SIZE; index++) {
                for(int i = 0; i < 12; i++) {
                    outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                }
            }
        }
    }
}
