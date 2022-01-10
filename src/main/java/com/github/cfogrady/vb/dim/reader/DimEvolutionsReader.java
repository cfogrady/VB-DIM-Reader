package com.github.cfogrady.vb.dim.reader;

import com.github.cfogrady.vb.dim.reader.content.DimEvolutionRequirements;

import java.util.ArrayList;
import java.util.List;

class DimEvolutionsReader {
    static DimEvolutionRequirements dimEvolutionRequirementsFromBytes(byte[] bytes) {
        List<DimEvolutionRequirements.DimEvolutionRequirementBlock> evolutionRequirementBlocks = new ArrayList<>();
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        boolean onlyZeroRow = false;
        for(int index = 0; index < values.length-12 && !onlyZeroRow; index+=12) {
            onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 12);
            if(!onlyZeroRow) {
                DimEvolutionRequirements.DimEvolutionRequirementBlock block = DimEvolutionRequirements.DimEvolutionRequirementBlock.builder()
                        .evolveFromStatIndex(values[index])
                        .hoursUntilEvolution(values[index+1])
                        .vitalRequirements(values[index+2])
                        .trophyRequirement(values[index+4])
                        .battleRequirement(values[index+6])
                        .winRatioRequirement(values[index+8])
                        .evolveToStatIndex(values[index+10])
                        .build();
                evolutionRequirementBlocks.add(block);
            }
        }
        return DimEvolutionRequirements.builder().evolutionRequirementBlocks(evolutionRequirementBlocks).build();
    }
}
