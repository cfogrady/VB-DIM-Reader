package com.github.cfogrady.vb.dim.header;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder=true)
@Data
@EqualsAndHashCode(callSuper=true)
public class BemHeader extends DimHeader {
    private final byte[] bemFlags;
}
