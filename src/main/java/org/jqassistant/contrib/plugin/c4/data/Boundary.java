package org.jqassistant.contrib.plugin.c4.data;

import lombok.experimental.SuperBuilder;
import org.apache.commons.compress.utils.Sets;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
