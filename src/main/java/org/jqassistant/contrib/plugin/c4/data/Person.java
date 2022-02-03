package org.jqassistant.contrib.plugin.c4.data;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Person extends AbstractBuildingBlock {

    @Override
    String getPrimaryElementTypeLabel() {
        return "Person";
    }

}
