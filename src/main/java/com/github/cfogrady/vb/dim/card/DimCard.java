package com.github.cfogrady.vb.dim.card;

import com.github.cfogrady.vb.dim.adventure.DimAdventures;
import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.fusion.DimFusions;
import com.github.cfogrady.vb.dim.fusion.DimSpecificFusions;
import com.github.cfogrady.vb.dim.header.DimHeader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.DimEvolutionRequirements;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder=true)
public class DimCard implements Card {
	private final DimHeader dimHeader;
	private final DimStats dimStats;
	private final DimEvolutionRequirements dimEvolutionRequirements;
	private final DimAdventures dimAdventures;
	private final DimFusions dimFusions;
	private final DimSpecificFusions dimSpecificFusion;
	private final int checksum;
	private final int calculatedCheckSum;
	private final SpriteData spriteData;

	@Override
	public CardType getContentType() {
		return CardType.DIM;
	}
}
