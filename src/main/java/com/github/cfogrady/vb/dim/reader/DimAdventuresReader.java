package com.github.cfogrady.vb.dim.reader;

import com.github.cfogrady.vb.dim.reader.content.DimAdventures;

import java.util.ArrayList;
import java.util.List;

class DimAdventuresReader {
    static DimAdventures dimAdventuresFromBytes(byte[] bytes, Integer maxStages) {
        List<DimAdventures.DimAdventureBlock> adventureBlocks = new ArrayList<>(15);
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        boolean onlyZeroRow = false;
        int indexLimit = maxStages != null ? maxStages*5 : values.length;
        for(int index = 0; index < indexLimit-5 && !onlyZeroRow; index+=5) {
            onlyZeroRow = ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 5);
            if(!onlyZeroRow) {
                DimAdventures.DimAdventureBlock block = DimAdventures.DimAdventureBlock.builder()
                        .steps(values[index])
                        .bossStatsIndex(values[index+1])
                        .bossDp(values[index+2])
                        .bossHp(values[index+3])
                        .bossAp(values[index+4])
                        .build();
                adventureBlocks.add(block);
            }
        }
        return DimAdventures.builder().adventureBlocks(adventureBlocks).build();
    }
}
