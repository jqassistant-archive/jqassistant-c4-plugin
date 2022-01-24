package org.jqassistant.contrib.plugin.c4diagram.data;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class C4System extends C4Element {
    @Override
    String getPrimaryElementTypeLabel() {
        return "System";
    }
}
