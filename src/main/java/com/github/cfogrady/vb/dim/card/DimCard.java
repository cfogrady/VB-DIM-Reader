package com.github.cfogrady.vb.dim.card;

import com.github.cfogrady.vb.dim.adventure.DimAdventures;
import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.fusion.DimFusions;
import com.github.cfogrady.vb.dim.fusion.DimSpecificFusions;
import com.github.cfogrady.vb.dim.header.DimHeader;
import com.github.cfogrady.vb.dim.transformation.DimEvolutionRequirements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class DimCard extends Card<
		DimHeader,
		DimStats,
		DimEvolutionRequirements,
		DimAdventures,
		DimFusions,
		DimSpecificFusions> { }
