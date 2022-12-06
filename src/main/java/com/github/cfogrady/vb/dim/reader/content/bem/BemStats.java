package com.github.cfogrady.vb.dim.reader.content.bem;

import com.github.cfogrady.vb.dim.reader.content.DimStats;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class BemStats {
    public static final int VB_TABLE_SIZE = 23;

    @Data
    @Builder(toBuilder = true)
    public static class BemStatBlock {
        private final int unknown;
        private final int stage;
        private final int attribute;
        private final int type;
        private final int smallAttackId;
        private final int bigAttackId;
        private final int bp;
        private final int hp;
        private final int ap;
        private final int firstPoolBattleChance;
        private final int secondPoolBattleChance;
        private final int thirdPoolBattleChance;
    }

    private final List<BemStats.BemStatBlock> statBlocks;
}
