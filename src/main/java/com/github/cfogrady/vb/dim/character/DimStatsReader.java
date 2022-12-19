package com.github.cfogrady.vb.dim.character;

import com.github.cfogrady.vb.dim.util.ByteUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DimStatsReader {
    public static DimStats dimStatsFromBytes(byte[] bytes) {
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        List<DimStats.DimStatBlock> statBlocks = new ArrayList<>(DimStats.VB_TABLE_SIZE);
        int index = 0;
        boolean onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 12);
        int dummyRows = 0;
        while(!onlyZeroRow) {
            if(!ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 12)) {
                if(values[index] > 5) {
                    log.warn("Row found with values but invalid stage! There are two official GP DIMs with this bug. Treating as a dummy row still");
                    dummyRows++;
                } else {
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
            } else {
                dummyRows++;
            }
            index += 12;
            onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 12); //find out if the next row is only zeros
        }
        return DimStats.builder().statBlocks(statBlocks).dummyRows(dummyRows).build();
    }
}
