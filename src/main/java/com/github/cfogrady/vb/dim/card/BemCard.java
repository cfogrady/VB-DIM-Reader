package com.github.cfogrady.vb.dim.card;

import com.github.cfogrady.vb.dim.adventure.BemAdventureLevels;
import com.github.cfogrady.vb.dim.character.BemCharacterStats;
import com.github.cfogrady.vb.dim.fusion.AttributeFusions;
import com.github.cfogrady.vb.dim.fusion.BemSpecificFusions;
import com.github.cfogrady.vb.dim.header.BemHeader;
import com.github.cfogrady.vb.dim.transformation.BemTransformationRequirements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BemCard extends Card<
        BemHeader,
        BemCharacterStats,
        BemTransformationRequirements,
        BemAdventureLevels,
        AttributeFusions,
        BemSpecificFusions> { }
