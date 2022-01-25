package org.jqassistant.contrib.plugin.c4.data;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class C4Container extends C4Element {
    @Override
    String getPrimaryElementTypeLabel() {
        return "Container";
    }
}
