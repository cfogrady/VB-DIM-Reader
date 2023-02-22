package com.github.cfogrady.vb.dim.adventure;

import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class BemAdventuresReader {
    public BemAdventureLevels readAdventures(ByteOffsetInputStream generalStream) {
        try {
            RelativeByteOffsetInputStream relativeInputStream = new RelativeByteOffsetInputStream(generalStream);
            List<BemAdventureLevels.BemAdventureLevel> levels = new ArrayList<>(BemAdventureConstants.MAX_TABLE_SIZE);
            int[] values = getRowValues(relativeInputStream);
            boolean validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            while (validRow) {
                levels.add(BemAdventureLevels.BemAdventureLevel.builder()
                        .steps(values[0])
                        .bossCharacterIndex(values[1])
                        .showBossIdentity(values[2])
                        .bossDp(values[3])
                        .bossHp(values[4])
                        .bossAp(values[5])
                        .smallAttackId(values[6])
                        .bigAttackId(values[7])
                        .background1(values[8])
                        .background2(values[9])
                        .giftCharacterIndex(values[10])
                        .build());
                values = getRowValues(relativeInputStream);
                validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            }
            return BemAdventureLevels.builder().levels(levels).build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private int[] getRowValues(RelativeByteOffsetInputStream relativeInputStream) throws IOException {
        byte[] rowBytes = relativeInputStream.readNBytes(BemAdventureConstants.ROW_SIZE * 2);
        return ByteUtils.getUnsigned16Bit(rowBytes);
    }
}
