package com.github.cfogrady.vb.dim.fusion;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BemSpecificFusions extends SpecificFusions<BemSpecificFusions.BemSpecificFusionEntry> {
    @Data
    @SuperBuilder(toBuilder = true)
    @EqualsAndHashCode(callSuper = true)
    public static class BemSpecificFusionEntry extends SpecificFusions.SpecificFusionEntry {
        private final int fromBemId;
        private final int toBemId;

        public int getBackupBemId() {
            return getBackupDimId();
        }
    }
}
