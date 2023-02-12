package com.github.cfogrady.vb.dim.character;

import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class BemCharacterReader {
    public BemCharacterStats readCharacterStats(ByteOffsetInputStream generalInputStream) {
        try {
            RelativeByteOffsetInputStream relativeInputStream = new RelativeByteOffsetInputStream(generalInputStream);
            List<BemCharacterStats.BemCharacterStatEntry> entries = new ArrayList<>(BemCharacterConstants.MAX_CHARACTERS);
            int[] values = getRowValues(relativeInputStream);
            boolean validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            while (validRow) {
                BemCharacterStats.BemCharacterStatEntry entry = BemCharacterStats.BemCharacterStatEntry.builder()
                        .spriteResizeFlag(values[0])
                        .stage(values[1])
                        .attribute(values[2])
                        .type(values[3])
                        .smallAttackId(values[4])
                        .bigAttackId(values[5])
                        .dp(values[6])
                        .hp(values[7])
                        .ap(values[8])
                        .firstPoolBattleChance(values[9])
                        .secondPoolBattleChance(values[10])
                        .thirdPoolBattleChance(values[11])
                        .build();
                entries.add(entry);
                values = getRowValues(relativeInputStream);
                validRow = !ByteUtils.onlyZerosOrMaxValuesInArray(values);
            }
            return BemCharacterStats.builder().characterEntries(entries).build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private int[] getRowValues(RelativeByteOffsetInputStream relativeInputStream) throws IOException {
        byte[] rowBytes = relativeInputStream.readNBytes(BemCharacterConstants.ROW_SIZE * 2);
        return ByteUtils.getUnsigned16Bit(rowBytes);
    }
}
