package com.github.cfogrady.vb.dim.reader.content.bem;

import com.github.cfogrady.vb.dim.reader.content.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder=true)
public class BemContent implements CardContent {
    private final DimHeader dimHeader;
    private final BemStats bemStats;
    private final DimEvolutionRequirements dimEvolutionRequirements;
    private final DimAdventures dimAdventures;
    private final DimFusions dimFusions;
    private final DimSpecificFusions dimSpecificFusion;
    private final int checksum;
    private final int calculatedCheckSum;
    private final SpriteData spriteData;

    @Override
    public ContentType getContentType() {
        return ContentType.BEM;
    }
}
