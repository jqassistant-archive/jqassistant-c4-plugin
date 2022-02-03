package org.jqassistant.contrib.plugin.c4.data;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Component extends AbstractBuildingBlock {
    @Override
    String getPrimaryElementTypeLabel() {
        return "Component";
    }
}
