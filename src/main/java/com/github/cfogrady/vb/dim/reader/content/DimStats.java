package com.github.cfogrady.vb.dim.reader.content;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class DimStats {

	public static final int VB_TABLE_SIZE = 17;

	@Data
	@Builder(toBuilder = true)
	public static class DimStatBlock {
		private final int stage;
		private final boolean unlockRequired;
		private final int attribute;
		private final int disposition;
		private final int smallAttackId;
		private final int bigAttackId;
		private final int dpStars;
		private final int dp;
		private final int hp;
		private final int ap;
		private final int firstPoolBattleChance;
		private final int secondPoolBattleChance;
	}
	
	private final List<DimStatBlock> statBlocks;
	private final int dummyRows;

}
