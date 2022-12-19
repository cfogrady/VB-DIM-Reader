package com.github.cfogrady.vb.dim.transformation;

import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;

import java.io.IOException;

public class EvolutionsWriter {
    public static void writeEvolutions(DimEvolutionRequirements evolutionRequirements, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x40000);
        int currentIndex = 0;
        for(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementEntry : evolutionRequirements.getEvolutionRequirementBlocks()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(evolutionRequirementEntry.getEvolveFromStatIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(evolutionRequirementEntry.getHoursUntilEvolution()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(evolutionRequirementEntry.getVitalRequirements()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(evolutionRequirementEntry.getTrophyRequirement()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(evolutionRequirementEntry.getBattleRequirement()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(evolutionRequirementEntry.getWinRatioRequirement()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(evolutionRequirementEntry.getEvolveToStatIndex()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
            currentIndex++;
        }
        if(evolutionRequirements.getDummyRows() > 0 && currentIndex < DimEvolutionRequirements.VB_TABLE_SIZE) {
            for(int index = 0; index < evolutionRequirements.getDummyRows() && currentIndex + index < DimEvolutionRequirements.VB_TABLE_SIZE; index++) {
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(0));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(0));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(0));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(0));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
                outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
            }
        }
    }
}
