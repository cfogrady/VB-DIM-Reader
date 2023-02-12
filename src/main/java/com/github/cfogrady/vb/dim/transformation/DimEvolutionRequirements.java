package com.github.cfogrady.vb.dim.transformation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder=true)
@Data
@EqualsAndHashCode(callSuper = true)
public class DimEvolutionRequirements extends TransformationRequirements<DimEvolutionRequirements.DimEvolutionRequirementBlock> {
	public static int VB_TABLE_SIZE = 34;

	@SuperBuilder(toBuilder=true)
	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class DimEvolutionRequirementBlock extends TransformationRequirements.TransformationRequirementsEntry {
		private final int hoursUntilEvolution;
		
		public boolean hasNextStage() {
			return this.hoursUntilEvolution < 0xFFFF;
		}
		public boolean hasNextIndependentStage() {
			return getToCharacterIndex() < 0xFFFF;
		}
	}

	private final int dummyRows;
}
