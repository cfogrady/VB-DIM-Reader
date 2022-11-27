package com.github.cfogrady.vb.dim.reader.content;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Builder(toBuilder=true)
@Data
@Slf4j
public class SpriteData {

	private final List<Sprite> sprites;
	private final String text;
	private final List<Integer> spriteChecksums;

	@Builder(toBuilder=true)
	@Data
	public static class Sprite {
		private final int width;
		private final int height;
		private final byte[] pixelData; //16-bit R5G6B5

		public byte[] get24BitRGB() {
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

		public byte[] getBGRA() {
			byte[] bgra = new byte[(pixelData.length/2)*4];
			for(int pixel = 0; pixel < width*height; pixel++) {
				int byte0 = pixelData[pixel*2+1] & 0xFF;
				int byte1 = pixelData[pixel*2] & 0xFF;
				int red = (byte0 & 0b11111000) >> 3;
				int blue = byte1 & 0b00011111;
				int green = ((byte0 & 0b00000111) << 3) |
						((byte1 & 0b11100000) >> 5);
				red = scaleTo24BitColor(red, 5);
				green = scaleTo24BitColor(green, 6);
				blue = scaleTo24BitColor(blue, 5);
				int alpha = 255;
				if(red == 0 && blue == 0 && green == 255) {
					alpha = 0;
					green = 0;
				}
				bgra[pixel*4+0] = (byte) blue; // blue
				bgra[pixel*4+1] = (byte) green;// green
				bgra[pixel*4+2] = (byte) red; // red
				bgra[pixel*4+3] = (byte) alpha;
			}
			return bgra;
		}
	}

	private static int scaleTo24BitColor(int color, int fromBits) {
		int maxValueOld = (int) Math.pow(2, fromBits) -1;
		int fullColor = (color * 255) / maxValueOld;
		return fullColor;
	}

}
