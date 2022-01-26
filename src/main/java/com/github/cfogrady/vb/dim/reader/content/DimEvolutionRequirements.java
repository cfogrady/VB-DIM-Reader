package com.github.cfogrady.vb.dim.reader.content;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
public class DimEvolutionRequirements {
	public static int VB_TABLE_SIZE = 34;

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
}
