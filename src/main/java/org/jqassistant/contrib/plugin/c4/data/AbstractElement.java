package org.jqassistant.contrib.plugin.c4.data;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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

    abstract Set<String> getAdditionalStereotypes();

}
