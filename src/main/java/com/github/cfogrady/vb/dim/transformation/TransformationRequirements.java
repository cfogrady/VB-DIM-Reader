package com.github.cfogrady.vb.dim.transformation;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
public class TransformationRequirements<T extends TransformationRequirements.TransformationRequirementsEntry> {

    @Data
    @SuperBuilder(toBuilder = true)
    public static class TransformationRequirementsEntry {
        private final int fromCharacterIndex;
        private final int requiredVitalValues; // 0 for BEM Fusion
        private final int requiredTrophies; // 0 for BEM fusion
        private final int requiredBattles; // 0 for BEM fusion
        private final int requiredWinRatio; // 0 for BEM fusion
        private final int toCharacterIndex; // 0xFFFF for BEM fusion
    }

    private final List<T> transformationEntries;
}
