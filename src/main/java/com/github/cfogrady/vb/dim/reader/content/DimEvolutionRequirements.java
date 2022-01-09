package com.github.cfogrady.vb.dim.reader.content;

import java.util.ArrayList;
import java.util.List;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
public class DimEvolutionRequirements {
	@Builder(toBuilder=true)
	@Data
	public static class DimEvolutionRequirementBlock {
		private final int evolveFromStatIndex;
		private final int hoursUntilEvolution;
		private final int vitalRequirements;
		private final int trophyRequirement;
		private final int battleRequirement;
		private final int winRatioRequirement;
		private final int evolveToStatIndex;
		
		public boolean hasNextStage() {
			return this.hoursUntilEvolution < 0xFFFF;
		}
		
		public boolean hasNextIndependentStage() {
			return this.evolveToStatIndex < 0xFFFF;
		}
	}
	
	private final List<DimEvolutionRequirementBlock> evolutionRequirementBlocks;

	public static DimEvolutionRequirements dimEvolutionRequirementsFromBytes(byte[] bytes, ChecksumBuilder checksumBuilder) {
		List<DimEvolutionRequirementBlock> evolutionRequirementBlocks = new ArrayList<>();
		int[] values = ByteUtils.getUnsigned16Bit(bytes);
		checksumBuilder.add16BitInts(values);
		boolean onlyZeroRow = false;
		for(int index = 0; index < 0x10000/2 && !onlyZeroRow; index+=12) {
			onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 12);
			if(!onlyZeroRow) {
				DimEvolutionRequirementBlock block = DimEvolutionRequirementBlock.builder()
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
