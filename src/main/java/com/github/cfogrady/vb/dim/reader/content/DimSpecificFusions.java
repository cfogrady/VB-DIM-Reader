package com.github.cfogrady.vb.dim.reader.content;

import java.util.List;

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
}
