package com.github.cfogrady.vb.dim.reader.content.bem;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class BemAdventureLevels {
    @Data
    @Builder(toBuilder = true)
    public static class BemAdventureLevel {
        private final int steps;
        private final int bossCharacterIndex;
        private final int showBossIdentity; //1 shows the boss, 2 shows OS secret character icon
        private final int bp;
        private final int hp;
        private final int ap;
        private final int smallAttackId; //40+ is BEM specific attack sprite
        private final int bigAttackId; //22+ is BEM specific attack sprite
        private final int background1;
        private final int background2;
        private final int giftCharacterIndex;
    }

    private final List<BemAdventureLevel> levels;
}
