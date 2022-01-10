package com.github.cfogrady.vb.dim.reader.content;

import java.util.List;

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

		byte[] get24BitRGB() {
			byte[] rgb = new byte[(pixelData.length/2)*3];
			for(int pixel = 0; pixel < width*height; pixel++) {
				int red = (pixelData[pixel*2 + 1] & 0b11111000) >> 3;
				int blue = pixelData[pixel*2] & 0b00011111;
				int green = ((pixelData[pixel*2 + 1] & 0b00000111) << 3) |
						((pixelData[pixel*2] & 0b11100000) >> 2);
				rgb[pixel*3] = (byte) scaleTo24BitColor(red, 5);
				rgb[pixel*3 + 1] = (byte) scaleTo24BitColor(green, 6);
				rgb[pixel*3 + 2] = (byte) scaleTo24BitColor(blue, 5);
			}
			return rgb;
		}
	}
	
	private final List<Sprite> sprites;

	private static int scaleTo24BitColor(int color, int fromBits) {
		int maxValueOld = (int) Math.pow(2, fromBits) -1;
		int fullColor = (color * 255) / maxValueOld;
		return fullColor;
	}

}
