package com.github.cfogrady.vb.dim.reader.content;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder=true)
public class DimContent {
	private final DimHeader dimHeader;
	private final DimStats dimStats;
	private final DimEvolutionRequirements dimEvolutionRequirements;
	private final DimAdventures dimAdventures;
	private final DimFusions dimFusions;
	private final DimSpecificFusions dimSpecificFusion;
	private final int checksum;
	private final SpriteData spriteData;
}
