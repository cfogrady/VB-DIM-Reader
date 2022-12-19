package com.github.cfogrady.vb.dim.character;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class BemCharacterStats {
    public static final int VB_TABLE_SIZE = 23;

    @Data
    @Builder(toBuilder = true)
    public static class BemCharacterStatEntry {
        private final int spriteResizeFlag; //seems to always be 2 for BE memories.
        private final int stage;
        private final int attribute;
        private final int type;
        private final int smallAttackId;
        private final int bigAttackId;
        private final int bp;
        private final int hp;
        private final int ap;
        private final int firstPoolBattleChance; //stage 2-3
        private final int secondPoolBattleChance; //stage 4-5
        private final int thirdPoolBattleChance; //stage 6+
    }

    private final List<BemCharacterStatEntry> characterEntries;
}
