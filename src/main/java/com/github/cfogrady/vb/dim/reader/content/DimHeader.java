package com.github.cfogrady.vb.dim.reader.content;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;

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
	
	public static DimHeader dimHeaderFromBytes(byte[] bytes, ChecksumBuilder checksumBuilder) {
		if(bytes.length < 0x102F) {
			throw new IllegalArgumentException("Not enough bytes for header!");
		}
		int[] values = ByteUtils.getUnsigned16Bit(bytes);
		checksumBuilder.add16BitInts(values);
		return DimHeader.builder()
				.text(new String(Arrays.copyOfRange(bytes, 0x10, 0x2F)))
				.dimId(values[0x32/2])
				.productionYear(values[0x36/2])
				.productionMonth(values[0x38/2])
				.productionDay(values[0x3a/2])
				.revisionNumber(values[0x3c/2])
				.headerSignature(Arrays.copyOfRange(bytes, 0x40, 0x5f))
				.has0x8fSet(bytes[0x8f] != 0)
				.spriteSignature(Arrays.copyOfRange(bytes, 0x1010, 0x102f))
				.build();
	}
	
}
