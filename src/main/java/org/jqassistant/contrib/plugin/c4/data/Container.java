package org.jqassistant.contrib.plugin.c4.data;

import lombok.experimental.SuperBuilder;

/**
 * Representation of a C4 Container.
 *
 * @author Stephan Pirnbaum
 */
@SuperBuilder
public class Container extends AbstractBuildingBlock {

    @Override
    String getPrimaryElementTypeLabel() {
        return "Container";
    }

}
