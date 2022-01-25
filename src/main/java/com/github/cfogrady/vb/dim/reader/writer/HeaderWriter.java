package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimHeader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HeaderWriter {
    static void writeHeader(DimHeader headerData, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x10);
        log.info("Text: {}", headerData.getText());
        outputStreamWithNot.writeBytes(headerData.getText().getBytes());
        outputStreamWithNot.writeZerosUntilOffset(0x32);
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getDimId()));
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getDimId()));
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionYear()));
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionMonth()));
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionDay()));
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getRevisionNumber()));
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(0));
        outputStreamWithNot.writeBytes(headerData.getHeaderSignature());
        outputStreamWithNot.writeZerosUntilOffset(0x8e);
        if(headerData.isHas0x8fSet()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(64768));
        }
        outputStreamWithNot.writeZerosUntilOffset(0x1010);
        outputStreamWithNot.writeBytes(headerData.getSpriteSignature());
    }
}
