package org.jqassistant.plugin.c4.data;

import lombok.experimental.SuperBuilder;

/**
 * Representation of a C4 Component.
 *
 * @author Stephan Pirnbaum
 */
@SuperBuilder
public class Component extends AbstractBuildingBlock {

    @Override
    String getPrimaryElementTypeLabel() {
        return "Component";
    }

}
