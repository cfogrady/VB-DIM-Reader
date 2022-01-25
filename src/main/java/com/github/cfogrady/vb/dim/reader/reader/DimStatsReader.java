package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimStats;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class DimStatsReader {
    static DimStats dimStatsFromBytes(byte[] bytes, Integer entryLimit) {
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        List<DimStats.DimStatBlock> statBlocks = new ArrayList<>(17);
        boolean onlyZeroRow = false;
        int indexLimit = entryLimit != null ? entryLimit*12 : values.length-12;
        for(int index = 0; index < indexLimit && !onlyZeroRow; index+=12) {
            onlyZeroRow = ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 12);
            if(!onlyZeroRow) {
                DimStats.DimStatBlock block = DimStats.DimStatBlock.builder()
                        .stage(values[index])
                        .unlockRequired(values[index+1] == 1)
                        .attribute(values[index+2])
                        .disposition(values[index+3])
                        .smallAttackId(values[index+4])
                        .bigAttackId(values[index+5])
                        .dpStars(values[index+6])
                        .dp(values[index+7])
                        .hp(values[index+8])
                        .ap(values[index+9])
                        .firstPoolBattleChance(values[index+10])
                        .secondPoolBattleChance(values[index+11])
                        .build();
                statBlocks.add(block);
                log.debug("Stats Block: {}", block);
            }
        }
        return DimStats.builder().statBlocks(statBlocks).build();
    }
}
