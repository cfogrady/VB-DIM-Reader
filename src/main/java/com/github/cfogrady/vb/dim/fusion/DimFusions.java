package com.github.cfogrady.vb.dim.fusion;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class DimFusions extends AttributeFusions {
	public static final int VB_TABLE_SIZE = 10;
	private final int dummyRows;

}
