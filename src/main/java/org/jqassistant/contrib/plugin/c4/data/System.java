package org.jqassistant.contrib.plugin.c4.data;

import lombok.experimental.SuperBuilder;

/**
 * Representation of a C4 System.
 *
 * @author Stephan Pirnbaum
 */
@SuperBuilder
public class System extends AbstractBuildingBlock {

    @Override
    String getPrimaryElementTypeLabel() {
        return "System";
    }

}
