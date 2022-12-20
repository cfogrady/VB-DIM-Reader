package com.github.cfogrady.vb.dim.header;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
public class DimHeader {
	private final String text;
	private final int dimId;
	private final int productionYear;
	private final int productionMonth;
	private final int productionDay;
	private final int revisionNumber;
	
	private final byte[] headerSignature;
	private final boolean has0x8fSet;
	private final byte[] spriteSignature;

	public boolean hasSpriteSignature() {
		for(byte b : spriteSignature) {
			if(b != 0) {
				return true;
			}
		}
		return false;
	}
}
