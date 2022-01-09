package com.github.cfogrady.vb.dim.reader.content;

import java.util.ArrayList;
import java.util.List;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class DimStats {
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

	public static DimStats dimStatsFromBytes(byte[] bytes, ChecksumBuilder checksumBuilder, Integer entryLimit) {
		int[] values = ByteUtils.getUnsigned16Bit(bytes);
		checksumBuilder.add16BitInts(values);
		List<DimStatBlock> statBlocks = new ArrayList<>(17);
		boolean onlyZeroRow = false;
		int indexLimit = entryLimit != null ? entryLimit*12 : 0x10000/2;
		for(int index = 0; index < indexLimit && !onlyZeroRow; index+=12) {
			onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 12);
			if(!onlyZeroRow) {
				DimStatBlock block = DimStatBlock.builder()
						.stage(values[index])
						.unlockRequired(values[index+1] == 1)
						.attribute(values[index+2])
						.disposition(values[index+3])
						.smallAttackId(values[index+4])
						.bigAttackId(values[index+5])
						.dpStars(values[index+6])
						.dp(values[index+7])
						.hp(values[index+8])
						.ap(values[index+9])
						.firstPoolBattleChance(values[index+10])
						.secondPoolBattleChance(values[index+11])
						.build();
				statBlocks.add(block);
			}
		}
		return DimStats.builder().statBlocks(statBlocks).build();
	}
}
