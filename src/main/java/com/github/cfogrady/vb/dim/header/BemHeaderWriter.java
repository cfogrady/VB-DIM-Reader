package com.github.cfogrady.vb.dim.header;

import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class BemHeaderWriter {
    public void writeHeader(BemHeader headerData, ByteOffsetOutputStream outputStream) throws IOException {
        RelativeByteOffsetOutputStream relativeOutputStream = new RelativeByteOffsetOutputStream(outputStream);
        relativeOutputStream.writeZerosUntilOffset(0x10);
        log.info("Text: {}", headerData.getText());
        relativeOutputStream.writeBytes(headerData.getText().getBytes());
        relativeOutputStream.writeZerosUntilOffset(0x32);
        relativeOutputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getDimId()));
        relativeOutputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getDimId()&0xFF));
        relativeOutputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionYear()));
        relativeOutputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionMonth()));
        relativeOutputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getProductionDay()));
        relativeOutputStream.writeBytes(ByteUtils.convert16BitIntToBytes(headerData.getRevisionNumber()));
        relativeOutputStream.writeBytes(ByteUtils.convert16BitIntToBytes(0));
        relativeOutputStream.writeBytes(headerData.getHeaderSignature());
        relativeOutputStream.writeZerosUntilOffset(0x1000);
        relativeOutputStream.writeBytes(headerData.getBemFlags());
        relativeOutputStream.writeBytes(headerData.getSpriteSignature());
    }
}
