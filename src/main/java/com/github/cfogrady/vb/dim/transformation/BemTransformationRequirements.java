package com.github.cfogrady.vb.dim.transformation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BemTransformationRequirements extends TransformationRequirements<BemTransformationRequirements.BemTransformationRequirementEntry> {
    @Data
    @SuperBuilder(toBuilder = true)
    @EqualsAndHashCode(callSuper = true)
    public static class BemTransformationRequirementEntry extends TransformationRequirements.TransformationRequirementsEntry {
        private final int minutesUntilTransformation;
        private final int minimumMinuteOfHour; // Usually 0
        private final int maximumMinuteOfHour; // Usually 0xFFFF
        private final int requiredCompletedAdventureLevel; //0xFFFF if none
        private final int isNotSecret; //0 for secret, 1 for visible. 0xFFFF for fusion

        public int getRequiredPp() {
            return getRequiredTrophies();
        }
    }
}
