package com.github.cfogrady.vb.dim.character;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
public class CharacterStats<T extends CharacterStats.CharacterStatsEntry> {

    @Data
    @SuperBuilder(toBuilder = true)
    public static class CharacterStatsEntry {
        private final int stage;
        private final int attribute;
        private final int type;
        private final int smallAttackId;
        private final int bigAttackId;
        private final int dp;
        private final int hp;
        private final int ap;
        private final int firstPoolBattleChance; //stage 2-3
        private final int secondPoolBattleChance; //stage 4-5
    }

    private final List<T> characterEntries;
}
