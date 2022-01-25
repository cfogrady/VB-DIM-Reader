package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimEvolutionRequirements;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class DimEvolutionsReader {
    static DimEvolutionRequirements dimEvolutionRequirementsFromBytes(byte[] bytes) {
        List<DimEvolutionRequirements.DimEvolutionRequirementBlock> evolutionRequirementBlocks = new ArrayList<>();
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        boolean onlyZeroRow = false;
        for(int index = 0; index < values.length-12 && !onlyZeroRow; index+=12) {
            onlyZeroRow = ByteUtils.onlyZerosOrMaxValuesInRange(values, index, 12);
            if(!onlyZeroRow) {
                if(values[index+3] != 65535) {
                    log.warn("Unexpected value {} at index {}-{} in evolution table for DIM. Please make an issue with the DIM card in question on https://github.com/cfogrady/DIM-Modifier/issues so I purchase and analyze the card", values[index+3], index/12, 3);
                }
                if(values[index+5] != 65535) {
                    log.warn("Unexpected value {} at index {}-{} in evolution table for DIM. Please make an issue with the DIM card in question on https://github.com/cfogrady/DIM-Modifier/issues so I purchase and analyze the card", values[index+5], index/12, 5);
                }
                if(values[index+7] != 65535) {
                    log.warn("Unexpected value {} at index {}-{} in evolution table for DIM. Please make an issue with the DIM card in question on https://github.com/cfogrady/DIM-Modifier/issues so I purchase and analyze the card", values[index+7], index/12, 7);
                }
                if(values[index+9] != 65535) {
                    log.warn("Unexpected value {} at index {}-{} in evolution table for DIM. Please make an issue with the DIM card in question on https://github.com/cfogrady/DIM-Modifier/issues so I purchase and analyze the card", values[index+9], index/12, 9);
                }
                if(values[index+11] != 65535) {
                    log.warn("Unexpected value {} at index {}-{} in evolution table for DIM. Please make an issue with the DIM card in question on https://github.com/cfogrady/DIM-Modifier/issues so I purchase and analyze the card", values[index+11], index/12, 11);
                }
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
