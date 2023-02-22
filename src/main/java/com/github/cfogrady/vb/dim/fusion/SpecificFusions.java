package com.github.cfogrady.vb.dim.fusion;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
public class SpecificFusions <T extends SpecificFusions.SpecificFusionEntry> {

    @Data
    @SuperBuilder(toBuilder = true)
    public static class SpecificFusionEntry {
        private final int fromCharacterIndex;
        private final int toCharacterIndex;
        private final int backupDimId;
        private final int backupCharacterIndex;
    }

    private final List<T> entries;
}
