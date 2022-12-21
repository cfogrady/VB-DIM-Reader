package com.github.cfogrady.vb.dim.adventure;

import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;

import java.io.IOException;
import java.io.UncheckedIOException;

public class BemAdventureWriter {
    public void writeAdventures(BemAdventureLevels adventureLevels, ByteOffsetOutputStream generalOutputStream) {
        try {
            RelativeByteOffsetOutputStream relativeOutputStream = new RelativeByteOffsetOutputStream(generalOutputStream);
            for(BemAdventureLevels.BemAdventureLevel level : adventureLevels.getLevels()) {
                relativeOutputStream.write16BitInt(level.getSteps());
                relativeOutputStream.write16BitInt(level.getBossCharacterIndex());
                relativeOutputStream.write16BitInt(level.getShowBossIdentity());
                relativeOutputStream.write16BitInt(level.getBp());
                relativeOutputStream.write16BitInt(level.getHp());
                relativeOutputStream.write16BitInt(level.getAp());
                relativeOutputStream.write16BitInt(level.getSmallAttackId());
                relativeOutputStream.write16BitInt(level.getBigAttackId());
                relativeOutputStream.write16BitInt(level.getBackground1());
                relativeOutputStream.write16BitInt(level.getBackground2());
                relativeOutputStream.write16BitInt(level.getGiftCharacterIndex());
            }
        } catch(IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
