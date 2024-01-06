package com.github.cfogrady.vb.dim.header;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@SuperBuilder(toBuilder=true)
@Data
@EqualsAndHashCode(callSuper=true)
public class BemHeader extends DimHeader {
    private final byte[] bemFlags;

    public int getFranchiseId() {
        return bemFlags[2];
    }

    public boolean canBeInjured() {
        return bemFlags[4] != 0;
    }

    public String getMinFirmware() {
        byte[] firmwareBytes = Arrays.copyOfRange(bemFlags, 13, 16);
        return new String(firmwareBytes, StandardCharsets.US_ASCII);
    }
}
