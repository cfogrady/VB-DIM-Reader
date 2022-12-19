package com.github.cfogrady.vb.dim.card;

import com.github.cfogrady.vb.dim.adventure.BemAdventureLevels;
import com.github.cfogrady.vb.dim.character.BemCharacterStats;
import com.github.cfogrady.vb.dim.fusion.BemSpecificFusions;
import com.github.cfogrady.vb.dim.header.DimHeader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.BemTransformationRequirements;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder=true)
public class BemCard implements Card {
    private final DimHeader dimHeader;
    private final BemCharacterStats bemCharacterStats;
    private final BemTransformationRequirements bemTransformationRequirements;
    private final BemAdventureLevels bemAdventureLevels;
    private final BemSpecificFusions bemSpecificFusions;
    private final int checksum;
    private final int calculatedCheckSum;
    private final SpriteData spriteData;

    @Override
    public CardType getContentType() {
        return CardType.BEM;
    }
}
