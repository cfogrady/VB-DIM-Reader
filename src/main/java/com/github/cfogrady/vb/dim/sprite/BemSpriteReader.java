package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BemSpriteReader {
    private final DimSpritesReader dimSpritesReader;

    public BemSpriteReader() {
        dimSpritesReader = new DimSpritesReader(SpriteChecksumAreasCalculator.buildForBEM());
    }


    public List<SpriteData.SpriteDimensions> readSpriteDimensions(ByteOffsetInputStream generalInputStream) {
        try {
            RelativeByteOffsetInputStream relativeInputStream = new RelativeByteOffsetInputStream(generalInputStream);
            List<SpriteData.SpriteDimensions> dimensions = new ArrayList<>();
            int[] values = getDimensionRowValues(relativeInputStream);
            boolean validRow = !anyZeros(values);
            while (validRow) {
                dimensions.add(SpriteData.SpriteDimensions.builder()
                        .width(values[0])
                        .height(values[1])
                        .build());
                values = getDimensionRowValues(relativeInputStream);
                validRow = !anyZeros(values);
            }
            return dimensions;
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public SpriteData getSpriteData(ByteOffsetInputStream generalInputStream, List<SpriteData.SpriteDimensions> spriteDimensions) {
        try {
            return dimSpritesReader.spriteDataFromDimensionsAndStream(spriteDimensions, generalInputStream);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private int[] getDimensionRowValues(RelativeByteOffsetInputStream relativeInputStream) throws IOException {
        byte[] rowBytes = relativeInputStream.readNBytes(2 * 2);
        return ByteUtils.getUnsigned16Bit(rowBytes);
    }

    private boolean anyZeros(int[] values) {
        for(int value : values) {
            if(value == 0) {
                return true;
            }
        }
        return false;
    }
}
