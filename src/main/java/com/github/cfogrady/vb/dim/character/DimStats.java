package com.github.cfogrady.vb.dim.character;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class DimStats extends CharacterStats<DimStats.DimStatBlock> {

	public static final int VB_TABLE_SIZE = 17;

	@Data
	@SuperBuilder(toBuilder = true)
	@EqualsAndHashCode(callSuper = true)
	public static class DimStatBlock extends CharacterStats.CharacterStatsEntry {
		private final boolean unlockRequired;
		private final int dpStars;
	}

	private final int dummyRows;

}
