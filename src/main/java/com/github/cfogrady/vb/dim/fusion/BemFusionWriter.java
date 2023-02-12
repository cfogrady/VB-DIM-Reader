package com.github.cfogrady.vb.dim.fusion;

import com.github.cfogrady.vb.dim.card.BemCardConstants;
import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;

import java.io.IOException;
import java.io.UncheckedIOException;

public class BemFusionWriter {
    public void writeAttributeFusions(AttributeFusions attributeFusions, ByteOffsetOutputStream generalOutputStream) {
        try {
            int index = 0;
            for(AttributeFusions.AttributeFusionEntry entry : attributeFusions.getEntries()) {
                generalOutputStream.write16BitInt(entry.getCharacterIndex());
                generalOutputStream.write16BitInt(entry.getAttribute3Fusion());
                generalOutputStream.write16BitInt(entry.getAttribute2Fusion());
                generalOutputStream.write16BitInt(entry.getAttribute1Fusion());
                generalOutputStream.write16BitInt(entry.getAttribute4Fusion());
                index++;
            }
            for(int i = index; i < BemFusionConstants.MAX_ATTRIBUTE_TABLE_SIZE; i++) {
                writeInvalidRow(generalOutputStream, BemFusionConstants.ATTRIBUTE_TABLE_ROW_SIZE);
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public void writeSpecificFusions(BemSpecificFusions specificFusions, ByteOffsetOutputStream generalOutputStream) {
        try {
            int index = 0;
            for(BemSpecificFusions.BemSpecificFusionEntry entry : specificFusions.getEntries()) {
                generalOutputStream.write16BitInt(entry.getFromBemId());
                generalOutputStream.write16BitInt(entry.getFromCharacterIndex());
                generalOutputStream.write16BitInt(entry.getToBemId());
                generalOutputStream.write16BitInt(entry.getToCharacterIndex());
                generalOutputStream.write16BitInt(entry.getBackupDimId());
                generalOutputStream.write16BitInt(entry.getBackupCharacterIndex());
                index++;
            }
            for(int i = index; i < BemFusionConstants.MAX_SPECIFIC_TABLE_SIZE; i++) {
                writeInvalidRow(generalOutputStream, BemFusionConstants.SPECIFIC_TABLE_ROW_SIZE);
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private void writeInvalidRow(ByteOffsetOutputStream outputStream, int rowSize) throws IOException {
        for(int i = 0; i < rowSize; i++) {
            outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(BemCardConstants.NONE_VALUE));
        }
    }
}
