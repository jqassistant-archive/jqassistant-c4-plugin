package org.jqassistant.contrib.plugin.c4.data;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class System extends AbstractBuildingBlock {

    @Override
    String getPrimaryElementTypeLabel() {
        return "System";
    }
}
