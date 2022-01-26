package com.github.cfogrady.vb.dim.reader.content;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
public class DimFusions {
	public static final int VB_TABLE_SIZE = 10;

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

}
