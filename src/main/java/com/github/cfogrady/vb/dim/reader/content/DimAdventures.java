package com.github.cfogrady.vb.dim.reader.content;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
public class DimAdventures {
	public static final int VB_TABLE_SIZE = 15;

	@Builder(toBuilder=true)
	@Data
	public static class DimAdventureBlock {
		private final int steps;
		private final int bossStatsIndex;
		private final int bossDp;
		private final int bossHp;
		private final int bossAp;
	}
	
	private final List<DimAdventureBlock> adventureBlocks;
	private final int dummyRows;
}
