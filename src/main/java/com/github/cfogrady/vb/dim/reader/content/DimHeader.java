package com.github.cfogrady.vb.dim.reader.content;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
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
}
