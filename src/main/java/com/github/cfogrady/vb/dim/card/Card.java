package com.github.cfogrady.vb.dim.card;

import com.github.cfogrady.vb.dim.adventure.AdventureLevels;
import com.github.cfogrady.vb.dim.character.CharacterStats;
import com.github.cfogrady.vb.dim.fusion.AttributeFusions;
import com.github.cfogrady.vb.dim.fusion.SpecificFusions;
import com.github.cfogrady.vb.dim.header.DimHeader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.TransformationRequirements;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public abstract class Card<
        HeaderType extends DimHeader,
        CharacterStatsType extends CharacterStats,
        TransformationType extends TransformationRequirements,
        AdventureType extends AdventureLevels,
        FusionType extends AttributeFusions,
        SpecficFusionType extends SpecificFusions
        > {
    private final HeaderType header;
    private final CharacterStatsType characterStats;
    private final TransformationType transformationRequirements;
    private final AdventureType adventureLevels;
    private final FusionType attributeFusions;
    private final SpecficFusionType specificFusions;
    private final SpriteData spriteData;
    private final int checksum;
    private final int calculatedCheckSum;
}
