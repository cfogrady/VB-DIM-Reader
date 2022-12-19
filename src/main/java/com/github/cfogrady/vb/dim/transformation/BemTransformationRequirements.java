package com.github.cfogrady.vb.dim.transformation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class BemTransformationRequirements {
    @Data
    @Builder(toBuilder = true)
    public static class BemTransformationRequirementEntry {
        private final int fromCharacterIndex;
        private final int minutesUntilTransformation;
        private final int requiredVitalValues; // 0 for fusion
        private final int requiredPp; // 0 for fusion
        private final int requiredBattles; // 0 for fusion
        private final int requiredWinRatio; // 0 for fusion
        private final int minimumMinuteOfHour; // Usually 0
        private final int maximumMinuteOfHour; // Usually 0xFFFF
        private final int requiredCompletedAdventureLevel; //0xFFFF if none
        private final int toCharacterIndex; // 0xFFFF for fusion
        private final int isNotSecret; //0 for secret, 1 for visible. 0xFFFF for fusion
    }

    private final List<BemTransformationRequirementEntry> transformationEntries;
}
