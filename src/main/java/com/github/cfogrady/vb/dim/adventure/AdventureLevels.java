package com.github.cfogrady.vb.dim.adventure;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
public class AdventureLevels<T extends AdventureLevels.AdventureLevel> {

    @Data
    @SuperBuilder(toBuilder = true)
    public static class AdventureLevel {
        private final int steps;
        private final int bossCharacterIndex;
        private final int bossDp;
        private final int bossHp;
        private final int bossAp;
    }

    private final List<T> levels;
}
