package com.github.cfogrady.vb.dim.sprite;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SpriteWithIndex {
    private final SpriteData.Sprite sprite;
    private final int index;

    public int getSize() {
        return sprite.getByteCountAt16BitPerPixel();
    }

    public byte[] getPixelData() {
        return sprite.getPixelData();
    }
}
