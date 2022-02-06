package org.jqassistant.contrib.plugin.c4.data;

import lombok.experimental.SuperBuilder;

/**
 * Representation of a C4 Person.
 *
 * @author Stephan Pirnbaum
 */
@SuperBuilder
public class Person extends AbstractBuildingBlock {

    @Override
    String getPrimaryElementTypeLabel() {
        return "Person";
    }

}
