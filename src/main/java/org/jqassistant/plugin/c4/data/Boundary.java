package org.jqassistant.plugin.c4.data;

import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representation of a C4 boundary.
 *
 * @author Stephan Pirnbaum
 */
@SuperBuilder
public class Boundary extends AbstractElement {

    private final String type;

    @Override
    public String buildStringRepresentation() {
        return String.format("%s{%s%s}", buildLabelString(), buildAliasString(), buildNameString());
    }

    @Override
    Set<String> getAdditionalStereotypes() {
        Set<String> labels = new LinkedHashSet<>();
        labels.add("Boundary");
        if (type != null) {
            labels.add(type);
        }
        return labels;
    }

}
