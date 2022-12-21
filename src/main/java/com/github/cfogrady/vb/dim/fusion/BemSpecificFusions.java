package com.github.cfogrady.vb.dim.fusion;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class BemSpecificFusions {
    @Data
    @Builder(toBuilder = true)
    public static class BemSpecificFusionEntry {
        private final int fromBemId;
        private final int fromCharacterIndex;
        private final int toBemId;
        private final int toCharacterIndex;
        private final int backupBemId;
        private final int backupCharacterId;
    }

    private final List<BemSpecificFusionEntry> entries;
}
