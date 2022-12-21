package com.github.cfogrady.vb.dim.character;

import com.github.cfogrady.vb.dim.card.BemCardConstants;
import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;

import java.io.IOException;
import java.io.UncheckedIOException;

public class BemCharacterWriter {
    public void writeBemCharacters(BemCharacterStats characterStats, ByteOffsetOutputStream generalOutputStream) {
        try {
            int index = 0;
            for(BemCharacterStats.BemCharacterStatEntry entry : characterStats.getCharacterEntries()) {
                generalOutputStream.write16BitInt(entry.getSpriteResizeFlag());
                generalOutputStream.write16BitInt(entry.getStage());
                generalOutputStream.write16BitInt(entry.getAttribute());
                generalOutputStream.write16BitInt(entry.getType());
                generalOutputStream.write16BitInt(entry.getSmallAttackId());
                generalOutputStream.write16BitInt(entry.getBigAttackId());
                generalOutputStream.write16BitInt(entry.getBp());
                generalOutputStream.write16BitInt(entry.getHp());
                generalOutputStream.write16BitInt(entry.getAp());
                generalOutputStream.write16BitInt(entry.getFirstPoolBattleChance());
                generalOutputStream.write16BitInt(entry.getSecondPoolBattleChance());
                generalOutputStream.write16BitInt(entry.getThirdPoolBattleChance());
                index++;
            }
            for(int i = index; i < BemCharacterConstants.MAX_CHARACTERS; i++) {
                writeInvalidRow(generalOutputStream);
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private void writeInvalidRow(ByteOffsetOutputStream outputStream) throws IOException {
        for(int i = 0; i < BemCharacterConstants.ROW_SIZE; i++) {
            outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(BemCardConstants.NONE_VALUE));
        }
    }
}
