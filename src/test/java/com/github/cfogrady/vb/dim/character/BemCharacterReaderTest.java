package com.github.cfogrady.vb.dim.character;

import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BemCharacterReaderTest {
    private final Random random = new Random();
    private BemCharacterReader bemCharacterReader;
    private BemCharacterWriter bemCharacterWriter;

    @BeforeEach
    void setup() {
        this.bemCharacterReader = new BemCharacterReader();
        this.bemCharacterWriter = new BemCharacterWriter();
    }

    @Test
    void testBemCharacterValuesReadCorrectly() {
        BemCharacterStats expectedCharacterStats = createBemCharacterStats(13);
        byte[] table = getTableInBytes(expectedCharacterStats);
        Assertions.assertEquals(BemCharacterConstants.MAX_CHARACTERS*BemCharacterConstants.ROW_SIZE*2, table.length);
        BemCharacterStats readStats = bemCharacterReader.readCharacterStats(new RelativeByteOffsetInputStream(new ByteArrayInputStream(table)));
        Assertions.assertEquals(expectedCharacterStats.getCharacterEntries().size(), readStats.getCharacterEntries().size());
        for(int i = 0; i < readStats.getCharacterEntries().size(); i++) {
            BemCharacterStats.BemCharacterStatEntry expected = expectedCharacterStats.getCharacterEntries().get(i);
            BemCharacterStats.BemCharacterStatEntry read = readStats.getCharacterEntries().get(i);
            Assertions.assertEquals(expected.getSpriteResizeFlag(), read.getSpriteResizeFlag());
            Assertions.assertEquals(expected.getStage(), read.getStage());
            Assertions.assertEquals(expected.getAttribute(), read.getAttribute());
            Assertions.assertEquals(expected.getType(), read.getType());
            Assertions.assertEquals(expected.getSmallAttackId(), read.getSmallAttackId());
            Assertions.assertEquals(expected.getBigAttackId(), read.getBigAttackId());
            Assertions.assertEquals(expected.getBp(), read.getBp());
            Assertions.assertEquals(expected.getHp(), read.getHp());
            Assertions.assertEquals(expected.getAp(), read.getAp());
            Assertions.assertEquals(expected.getFirstPoolBattleChance(), read.getFirstPoolBattleChance());
            Assertions.assertEquals(expected.getSecondPoolBattleChance(), read.getSecondPoolBattleChance());
            Assertions.assertEquals(expected.getThirdPoolBattleChance(), read.getThirdPoolBattleChance());
        }
    }

    private BemCharacterStats createBemCharacterStats(int number) {
        List<BemCharacterStats.BemCharacterStatEntry> entries = new ArrayList<>(number);
        for(int i = 0; i < number; i++) {
            entries.add(BemCharacterStats.BemCharacterStatEntry.builder()
                            .spriteResizeFlag(2)
                            .stage(random.nextInt(4) + 2)
                            .attribute(random.nextInt(3) + 1)
                            .type(random.nextInt(5))
                            .smallAttackId(random.nextInt(39))
                            .bigAttackId(random.nextInt(21))
                            .dp(random.nextInt(10000))
                            .hp(random.nextInt(10000))
                            .ap(random.nextInt(10000))
                            .firstPoolBattleChance(random.nextInt(100))
                            .secondPoolBattleChance(random.nextInt(100))
                            .thirdPoolBattleChance(random.nextInt(100))
                    .build());
        }
        return BemCharacterStats.builder().characterEntries(entries).build();
    }

    private byte[] getTableInBytes(BemCharacterStats bemCharacterStats) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bemCharacterWriter.writeBemCharacters(bemCharacterStats, new RelativeByteOffsetOutputStream(outputStream));
        return outputStream.toByteArray();
    }
}
