package com.github.cfogrady.vb.dim.sprite;

import java.util.*;

public class SpriteFinder {
    // map size to IndexedSprites.
    // array of integers corresponding to sprite sizes
    // when last sprite of a size is used, the element is removed from the array.
    private final Map<Integer, LinkedList<SpriteWithIndex>> spritesBySize;
    private final TreeSet<Integer> spriteSizes;

    private SpriteFinder(Map<Integer, LinkedList<SpriteWithIndex>> spritesBySize, TreeSet<Integer> spriteSizes) {
        this.spritesBySize = spritesBySize;
        this.spriteSizes = spriteSizes;
    }

    public static SpriteFinder createSpriteFinder(List<SpriteData.Sprite> sprites) {
        Map<Integer, LinkedList<SpriteWithIndex>> spritesBySize = new HashMap<>();
        TreeSet<Integer> spriteSizes = new TreeSet<>();
        int i = 0;
        for(SpriteData.Sprite sprite : sprites) {
            int size = sprite.getByteCountAt16BitPerPixel();
            LinkedList<SpriteWithIndex> spritesAtSize = spritesBySize.get(size);
            if(spritesAtSize == null) {
                spritesAtSize = new LinkedList<>();
                spritesBySize.put(size, spritesAtSize);
            }
            spritesAtSize.addLast(SpriteWithIndex.builder()
                    .index(i)
                    .sprite(sprite)
                    .build());
            i++;
        }
        spriteSizes.addAll(spritesBySize.keySet());
        return new SpriteFinder(spritesBySize, spriteSizes);
    }

    public SpriteWithIndex getNextSmallestSize(int size) {
        Integer nextSmallestSize = spriteSizes.floor(size);
        if(nextSmallestSize == null) {
            return null;
        }
        return removeFromSizePool(nextSmallestSize);
    }

    public SpriteWithIndex getNextLargestSprite() {
        Integer largestSize = spriteSizes.last();
        return removeFromSizePool(largestSize);
    }

    public boolean isEmpty() {
        return spritesBySize.isEmpty();
    }

    private SpriteWithIndex removeFromSizePool(int size) {
        LinkedList<SpriteWithIndex> spritesAtSize = spritesBySize.get(size);
        SpriteWithIndex sprite = spritesAtSize.removeFirst();
        if(spritesAtSize.isEmpty()) {
            spriteSizes.remove(size);
            spritesBySize.remove(size);
        }
        return sprite;
    }
}
