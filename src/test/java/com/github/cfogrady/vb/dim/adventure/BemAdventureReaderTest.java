package com.github.cfogrady.vb.dim.adventure;

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

public class BemAdventureReaderTest {
    private final Random random = new Random();
    private BemAdventuresReader bemAdventuresReader;
    private BemAdventureWriter bemAdventureWriter;

    @BeforeEach
    void setup() {
        this.bemAdventuresReader = new BemAdventuresReader();
        this.bemAdventureWriter = new BemAdventureWriter();
    }

    @Test
    void testBemTransformationValuesReadCorrectly() {
        BemAdventureLevels expectedAdventureLevels = createBemAdventureLevels();
        byte[] table = getTableInBytes(expectedAdventureLevels);
        Assertions.assertEquals(BemAdventureConstants.MAX_TABLE_SIZE*BemAdventureConstants.ROW_SIZE*2, table.length);
        BemAdventureLevels readAdventures = bemAdventuresReader.readAdventures(new RelativeByteOffsetInputStream(new ByteArrayInputStream(table)));
        Assertions.assertEquals(expectedAdventureLevels.getLevels().size(), readAdventures.getLevels().size());
        for(int i = 0; i < readAdventures.getLevels().size(); i++) {
            BemAdventureLevels.BemAdventureLevel expected = expectedAdventureLevels.getLevels().get(i);
            BemAdventureLevels.BemAdventureLevel read = readAdventures.getLevels().get(i);
            Assertions.assertEquals(expected.getSteps(), read.getSteps());
            Assertions.assertEquals(expected.getBossCharacterIndex(), read.getBossCharacterIndex());
            Assertions.assertEquals(expected.getShowBossIdentity(), read.getShowBossIdentity());
            Assertions.assertEquals(expected.getBp(), read.getBp());
            Assertions.assertEquals(expected.getHp(), read.getHp());
            Assertions.assertEquals(expected.getAp(), read.getAp());
            Assertions.assertEquals(expected.getSmallAttackId(), read.getSmallAttackId());
            Assertions.assertEquals(expected.getBigAttackId(), read.getBigAttackId());
            Assertions.assertEquals(expected.getBackground1(), read.getBackground1());
            Assertions.assertEquals(expected.getBackground2(), read.getBackground2());
            Assertions.assertEquals(expected.getGiftCharacterIndex(), read.getGiftCharacterIndex());
        }
    }

    private BemAdventureLevels createBemAdventureLevels() {
        List<BemAdventureLevels.BemAdventureLevel> entries = new ArrayList<>(BemAdventureConstants.MAX_TABLE_SIZE);
        for(int i = 0; i < BemAdventureConstants.MAX_TABLE_SIZE; i++) {
            entries.add(BemAdventureLevels.BemAdventureLevel.builder()
                    .steps(random.nextInt(2401)+100)
                    .bossCharacterIndex(random.nextInt(23))
                    .showBossIdentity(random.nextInt(2))
                    .bp(random.nextInt(6000))
                    .hp(random.nextInt(6000))
                    .ap(random.nextInt(6000))
                    .smallAttackId(random.nextInt(39))
                    .bigAttackId(random.nextInt(21))
                    .background1(random.nextInt(6))
                    .background2(random.nextInt(6))
                    .giftCharacterIndex(random.nextInt(23))
                    .build());
        }
        return BemAdventureLevels.builder().levels(entries).build();
    }

    private byte[] getTableInBytes(BemAdventureLevels bemAdventureLevels) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bemAdventureWriter.writeAdventures(bemAdventureLevels, new RelativeByteOffsetOutputStream(outputStream));
        return outputStream.toByteArray();
    }
}
