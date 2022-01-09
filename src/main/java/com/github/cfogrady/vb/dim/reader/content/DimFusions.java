package com.github.cfogrady.vb.dim.reader.content;

import java.util.ArrayList;
import java.util.List;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
public class DimFusions {
	@Builder(toBuilder=true)
	@Data
	public static class DimFusionBlock {
		private final int statsIndex;
		private final int statsIndexForFusionWithType3;
		private final int statsIndexForFusionWithType2;
		private final int statsIndexForFusionWithType1;
		private final int statsIndexForFusionWithType4;
	}
	
	private final List<DimFusionBlock> fusionBlocks;

	public static DimFusions dimFusionsFromBytes(byte[] bytes, ChecksumBuilder checksumBuilder) {
		List<DimFusionBlock> fusionBlocks = new ArrayList<>();
		int[] values = ByteUtils.getUnsigned16Bit(bytes);
		checksumBuilder.add16BitInts(values);
		boolean onlyZeroRow = false;
		for(int index = 0; index < 0x10000/2 && !onlyZeroRow; index+=5) {
			onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 5);
			if(!onlyZeroRow) {
				DimFusionBlock block = DimFusionBlock.builder()
						.statsIndex(values[index])
						.statsIndexForFusionWithType3(values[index+1])
						.statsIndexForFusionWithType2(values[index+2])
						.statsIndexForFusionWithType1(values[index+3])
						.statsIndexForFusionWithType4(values[index+4])
						.build();
				fusionBlocks.add(block);
			}
		}
		return DimFusions.builder().fusionBlocks(fusionBlocks).build();
	}
}
