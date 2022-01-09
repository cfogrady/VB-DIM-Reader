package com.github.cfogrady.vb.dim.reader.content;

import java.util.ArrayList;
import java.util.List;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
public class DimSpecificFusions {
	@Builder(toBuilder=true)
	@Data
	public static class DimSpecificFusionBlock {
		private final int statsIndex;
		private final int statsIndexForFusionResult;
		private final int fusionDimId;
		private final int fusionDimSlotId;
	}
	
	private final List<DimSpecificFusionBlock> dimSpecificFusionBlocks;

	public static DimSpecificFusions dimSpecificFusionsFromBytes(byte[] bytes, ChecksumBuilder checksumBuilder, Integer maxDimSpecificFusions) {
		List<DimSpecificFusionBlock> dimSpecificFusionBlocks = new ArrayList<>();
		int[] values = ByteUtils.getUnsigned16Bit(bytes);
		checksumBuilder.add16BitInts(values);
		boolean onlyZeroRow = false;
		int indexLimit = maxDimSpecificFusions != null ? maxDimSpecificFusions*4 : (0x100000-0x80000)/2;
		for(int index = 0; index < indexLimit && !onlyZeroRow; index+=4) {
			onlyZeroRow = ByteUtils.onlyZerosInRange(values, index, 4);
			if(!onlyZeroRow) {
				DimSpecificFusionBlock block = DimSpecificFusionBlock.builder()
						.statsIndex(values[index])
						.statsIndexForFusionResult(values[index+1])
						.fusionDimId(values[index+2])
						.fusionDimSlotId(values[index+3])
						.build();
				dimSpecificFusionBlocks.add(block);
			}
		}
		return DimSpecificFusions.builder().dimSpecificFusionBlocks(dimSpecificFusionBlocks).build();
	}
}
