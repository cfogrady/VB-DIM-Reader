package com.github.cfogrady.vb.dim.reader.content;

import java.util.ArrayList;
import java.util.List;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
public class DimAdventures {
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

	public static DimAdventures dimAdventuresFromBytes(byte[] bytes, ChecksumBuilder checksumBuilder, Integer maxStages) {
		List<DimAdventureBlock> adventureBlocks = new ArrayList<>(15);
		int[] values = ByteUtils.getUnsigned16Bit(bytes);
		checksumBuilder.add16BitInts(values);
		boolean onlyZeroRow = false;
		int indexLimit = maxStages != null ? maxStages*5 : 0x10000/2;
		for(int index = 0; index < indexLimit && !onlyZeroRow; index+=5) {
			onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5);
			if(!onlyZeroRow) {
				DimAdventureBlock block = DimAdventureBlock.builder()
						.steps(values[index])
						.bossStatsIndex(values[index+1])
						.bossDp(values[index+2])
						.bossHp(values[index+3])
						.bossAp(values[index+4])
						.build();
				adventureBlocks.add(block);
			}
		}
		return DimAdventures.builder().adventureBlocks(adventureBlocks).build();
	}
}
