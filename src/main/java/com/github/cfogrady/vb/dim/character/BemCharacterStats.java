package com.github.cfogrady.vb.dim.character;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BemCharacterStats extends CharacterStats<BemCharacterStats.BemCharacterStatEntry> {
    public static final int VB_TABLE_SIZE = 23;

    @Data
    @SuperBuilder(toBuilder = true)
    @EqualsAndHashCode(callSuper = true)
    public static class BemCharacterStatEntry extends CharacterStats.CharacterStatsEntry {
        private final int spriteResizeFlag; //seems to always be 2 for BE memories.
        private final int thirdPoolBattleChance; //stage 6+

        public int getBp() {
            return getDp();
        }
    }
}
