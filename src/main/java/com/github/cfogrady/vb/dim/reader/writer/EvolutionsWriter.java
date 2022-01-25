package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimEvolutionRequirements;

import java.io.IOException;

public class EvolutionsWriter {
    public static void writeEvolutions(DimEvolutionRequirements evolutionRequirements, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x40000);
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
        }
    }
}
