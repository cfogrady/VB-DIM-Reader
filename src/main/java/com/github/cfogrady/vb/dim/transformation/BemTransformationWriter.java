package com.github.cfogrady.vb.dim.transformation;

import com.github.cfogrady.vb.dim.card.BemCardConstants;
import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;

import java.io.IOException;
import java.io.UncheckedIOException;

public class BemTransformationWriter {
    public void writeBemTransformations(BemTransformationRequirements bemTransformationRequirements, ByteOffsetOutputStream generalOutputStream) {
        RelativeByteOffsetOutputStream relativeOutputStream = new RelativeByteOffsetOutputStream(generalOutputStream);
        try {
            int i = 0;
            for (BemTransformationRequirements.BemTransformationRequirementEntry entry : bemTransformationRequirements.getTransformationEntries()) {
                relativeOutputStream.write16BitInt(entry.getFromCharacterIndex());
                relativeOutputStream.write16BitInt(entry.getMinutesUntilTransformation());
                relativeOutputStream.write16BitInt(entry.getRequiredVitalValues());
                relativeOutputStream.write16BitInt(entry.getRequiredPp());
                relativeOutputStream.write16BitInt(entry.getRequiredBattles());
                relativeOutputStream.write16BitInt(entry.getRequiredWinRatio());
                relativeOutputStream.write16BitInt(entry.getMinimumMinuteOfHour());
                relativeOutputStream.write16BitInt(entry.getMaximumMinuteOfHour());
                relativeOutputStream.write16BitInt(entry.getRequiredCompletedAdventureLevel());
                relativeOutputStream.write16BitInt(entry.getToCharacterIndex());
                relativeOutputStream.write16BitInt(entry.getIsNotSecret());
                i++;
            }
            //populate the rest of the table with invalid rows
            for(int invalidRowIndex = i; invalidRowIndex < BemTransformationConstants.MAX_TABLE_SIZE; invalidRowIndex++) {
                writeInvalidRow(relativeOutputStream);
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private void writeInvalidRow(ByteOffsetOutputStream outputStream) throws IOException {
        for(int i = 0; i < BemTransformationConstants.ROW_SIZE; i++) {
            if(i < 2 || i > 6) {
                outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(BemCardConstants.NONE_VALUE));
            } else {
                outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(0));
            }
        }
    }
}
