package com.github.cfogrady.vb.dim.fusion;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
public class DimSpecificFusions {
	public static final int VB_TABLE_SIZE = 1;

	@Builder(toBuilder=true)
	@Data
	public static class DimSpecificFusionBlock {
		private final int statsIndex;
		private final int statsIndexForFusionResult;
		private final int fusionDimId;
		private final int fusionDimSlotId;
	}
	
	private final List<DimSpecificFusionBlock> dimSpecificFusionBlocks;
	private final int dummyRows;
}
