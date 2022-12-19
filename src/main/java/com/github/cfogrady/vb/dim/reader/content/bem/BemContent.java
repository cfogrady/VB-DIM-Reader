package com.github.cfogrady.vb.dim.reader.content.bem;

import com.github.cfogrady.vb.dim.reader.content.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder=true)
public class BemContent implements CardContent {
    private final DimHeader dimHeader;
    private final BemCharacterStats bemCharacterStats;
    private final BemTransformationRequirements bemTransformationRequirements;
    private final BemAdventureLevels bemAdventureLevels;
    private final BemSpecificFusions bemSpecificFusions;
    private final int checksum;
    private final int calculatedCheckSum;
    private final SpriteData spriteData;

    @Override
    public ContentType getContentType() {
        return ContentType.BEM;
    }
}
