package com.github.cfogrady.vb.dim.card;


import com.github.cfogrady.vb.dim.header.BemHeader;
import com.github.cfogrady.vb.dim.header.BemHeaderReader;
import com.github.cfogrady.vb.dim.util.InputStreamWithNot;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;

@RequiredArgsConstructor
public class BemCardReader {
    private final BemHeaderReader bemHeaderReader;

    public BemCardReader() {
        bemHeaderReader = new BemHeaderReader();
    }

    public BemCard readBemCard(byte[] headerBytes, InputStreamWithNot inputStream) {
        BemHeader header = bemHeaderReader.readBemHeaderFromHeaderBytes(headerBytes);
        try {
            inputStream.readToOffset(BemCardConstants.CHARACTER_SECTION_START);
            
            return BemCard.builder()
                    .bemHeader(header)
                    .build();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
