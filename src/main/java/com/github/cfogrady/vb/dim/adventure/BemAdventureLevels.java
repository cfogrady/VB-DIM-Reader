package com.github.cfogrady.vb.dim.adventure;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BemAdventureLevels extends AdventureLevels<BemAdventureLevels.BemAdventureLevel> {

    @Data
    @SuperBuilder(toBuilder = true)
    @EqualsAndHashCode(callSuper = true)
    public static class BemAdventureLevel extends AdventureLevels.AdventureLevel {
        private final int showBossIdentity; //1 shows the boss, 2 shows OS secret character icon
        private final int smallAttackId; //40+ is BEM specific attack sprite
        private final int bigAttackId; //22+ is BEM specific attack sprite
        private final int background1;
        private final int background2;
        private final int giftCharacterIndex;

        public int getBossBp() {
            return getBossDp();
        }
    }
}
