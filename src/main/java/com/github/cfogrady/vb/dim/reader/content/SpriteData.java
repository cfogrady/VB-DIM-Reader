package com.github.cfogrady.vb.dim.reader.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Builder(toBuilder=true)
@Data
@Slf4j
public class SpriteData {
	@Builder(toBuilder=true)
	@Data
	public static class Sprite {
		public final int width;
		public final int height;
		byte[] pixelData; //16-bit R5G6B5
	}
	
	private final List<Sprite> sprites;

	public static SpriteData spriteDataFromBytesAndStream(byte[] spriteDimensionBytes, InputStream spriteDataSection, ChecksumBuilder checksumBuilder) throws IOException {
		int[] spriteDimensions = ByteUtils.getUnsigned16Bit(spriteDimensionBytes);
		checksumBuilder.add16BitInts(spriteDimensions);
		checksumBuilder.addBytes(ByteUtils.applyNotOperation(spriteDataSection.readNBytes(0x18)));
		byte[] finalOffsetBytes = ByteUtils.applyNotOperation(spriteDataSection.readNBytes(4));
		checksumBuilder.addBytes(finalOffsetBytes);
		int finalOffset = ByteUtils.getIntsFromBytes(finalOffsetBytes)[0];
		checksumBuilder.addBytes(ByteUtils.applyNotOperation(spriteDataSection.readNBytes(0x48 - 0x1b)));
		byte[] numberOfSpriteBytes = ByteUtils.applyNotOperation(spriteDataSection.readNBytes(4));
		checksumBuilder.addBytes(numberOfSpriteBytes);
		int numberOfSprites = ByteUtils.getIntsFromBytes(numberOfSpriteBytes)[0];
		byte[] spriteOffsetBytes = ByteUtils.applyNotOperation(spriteDataSection.readNBytes(numberOfSprites*4));
		checksumBuilder.addBytes(spriteOffsetBytes);
		int[] spriteOffsets = ByteUtils.getIntsFromBytes(spriteOffsetBytes);
		List<Sprite> sprites = new ArrayList<>(numberOfSprites);
		int currentOffset = spriteOffsets[0];
		for(int i = 0; i < numberOfSprites; i++)
		{
			int width = spriteDimensions[i*2];
			int height = spriteDimensions[i*2 + 1];
			int expectedSize = width * height * 2;
			Sprite sprite = Sprite.builder()
					.width(width)
					.height(height)
					.pixelData(ByteUtils.applyNotOperation(spriteDataSection.readNBytes(expectedSize)))
					.build();
			checksumBuilder.addBytes(sprite.getPixelData());
			if(i != numberOfSprites-1) {
				if(sprite.getPixelData().length != spriteOffsets[i+1] - currentOffset) {
					log.error("Expected sprite size {} doesn't match delta from current offset {} to next offset {}", sprite.getPixelData().length, currentOffset, spriteOffsets[i+1]);
				}
				currentOffset = spriteOffsets[i+1];
			}
			sprites.add(sprite);
		}
		return SpriteData.builder().sprites(sprites).build();
	}
}
