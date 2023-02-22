package com.github.cfogrady.vb.dim.adventure;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder=true)
@Data
@EqualsAndHashCode(callSuper = true)
public class DimAdventures extends AdventureLevels<AdventureLevels.AdventureLevel> {
	public static final int VB_TABLE_SIZE = 15;
	private final int dummyRows;
}
