package com.github.cfogrady.vb.dim.header;

import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HeaderWriter {
    public static void writeHeader(DimHeader headerData, ByteOffsetOutputStream outputStream) throws IOException {
        outputStream.writeZerosUntilOffset(0x10);
        log.info("Text: {}", headerData.getText());
        outputStream.writeBytes(headerData.getText().getBytes());
        outputStream.writeZerosUntilOffset(0x32);
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getDimId()));
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getDimId()));
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionYear()));
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionMonth()));
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionDay()));
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getRevisionNumber()));
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(0));
        outputStream.writeBytes(headerData.getHeaderSignature());
        outputStream.writeZerosUntilOffset(0x8e);
        if(headerData.isHas0x8fSet()) {
            outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(64768));
        }
        outputStream.writeZerosUntilOffset(0x1010);
        outputStream.writeBytes(headerData.getSpriteSignature());
    }
}
