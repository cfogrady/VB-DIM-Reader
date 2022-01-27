package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimAdventures;

import java.util.ArrayList;
import java.util.List;

class DimAdventuresReader {
    static DimAdventures dimAdventuresFromBytes(byte[] bytes) {
        List<DimAdventures.DimAdventureBlock> adventureBlocks = new ArrayList<>(DimAdventures.VB_TABLE_SIZE);
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        int index = 0;
        boolean onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5);
        int dummyRows = 0;
        while(!onlyZeroRow) {
            if (!ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 5)) {
                DimAdventures.DimAdventureBlock block = DimAdventures.DimAdventureBlock.builder()
                        .steps(values[index])
                        .bossStatsIndex(values[index+1])
                        .bossDp(values[index+2])
                        .bossHp(values[index+3])
                        .bossAp(values[index+4])
                        .build();
                adventureBlocks.add(block);
            } else {
                dummyRows++;
            }
            index += 5;
            onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5); //find out if the next row is only zeros
        }
        return DimAdventures.builder().adventureBlocks(adventureBlocks).dummyRows(dummyRows).build();
    }
}
