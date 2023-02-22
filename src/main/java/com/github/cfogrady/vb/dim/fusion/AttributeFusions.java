package com.github.cfogrady.vb.dim.fusion;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
public class AttributeFusions {
    @Data
    @SuperBuilder(toBuilder = true)
    public static class AttributeFusionEntry {
        private final int characterIndex;
        private final int attribute3Fusion;
        private final int attribute2Fusion;
        private final int attribute1Fusion;
        private final int attribute4Fusion;
    }

    private final List<AttributeFusionEntry> entries;
}
