package com.github.cfogrady.vb.dim.header;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder=true)
@Data
public class BemHeader extends DimHeader {
    private final byte[] bemFlags;
}
