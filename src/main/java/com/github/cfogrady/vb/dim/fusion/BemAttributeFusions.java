package com.github.cfogrady.vb.dim.fusion;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class BemAttributeFusions {
    @Data
    @Builder(toBuilder = true)
    public static class BemAttributeFusionEntry {
        private final int characterIndex;
        private final int attribute3Fusion;
        private final int attribute2Fusion;
        private final int attribute1Fusion;
        private final int attribute4Fusion;
    }

    private final List<BemAttributeFusionEntry> entries;
}
