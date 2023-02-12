package com.github.cfogrady.vb.dim.adventure;

import com.github.cfogrady.vb.dim.util.ByteUtils;

import java.util.ArrayList;
import java.util.List;

public class DimAdventuresReader {
    public static DimAdventures dimAdventuresFromBytes(byte[] bytes) {
        List<DimAdventures.AdventureLevel> adventureBlocks = new ArrayList<>(DimAdventures.VB_TABLE_SIZE);
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        int index = 0;
        boolean onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5);
        int dummyRows = 0;
        while(!onlyZeroRow) {
            if (!ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 5)) {
                DimAdventures.AdventureLevel block = DimAdventures.AdventureLevel.builder()
                        .steps(values[index])
                        .bossCharacterIndex(values[index+1])
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
        return DimAdventures.builder().levels(adventureBlocks).dummyRows(dummyRows).build();
    }
}
