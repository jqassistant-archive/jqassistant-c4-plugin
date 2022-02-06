package org.jqassistant.contrib.plugin.c4.data;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representation of any C4 element.
 *
 * @author Stephan Pirnbaum
 */
@SuperBuilder
public abstract class AbstractElement {

    @Getter
    private final String alias;
    @Getter
    private final String name;
    @Builder.Default
    private final Set<String> stereotypes = new HashSet<>();
    @Getter
    private AbstractElement parent;

    /**
     * Creates a String representation of the C4 element for use in Cypher queries in the format: :Label*{Property*}
     *
     * @return The String representation.
     */
    public abstract String buildStringRepresentation();

    final String buildAliasString() {
        return "alias: \"" + this.alias + "\"";
    }

    final String buildNameString() {
        return ", name: \"" + this.name + "\"";
    }

    final String buildLabelString() {
        Set<String> labels = new LinkedHashSet<>();
        labels.add("C4");
        labels.addAll(getAdditionalStereotypes());
        labels.addAll(this.stereotypes);
        return ":" + String.join(":", labels);
    }

    /**
     * Get additional stereotypes of the C4 element for use as Cypher labels.
     *
     * @return The additional stereotypes.
     */
    abstract Set<String> getAdditionalStereotypes();

}
