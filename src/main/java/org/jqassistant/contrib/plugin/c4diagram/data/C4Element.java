package org.jqassistant.contrib.plugin.c4diagram.data;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuperBuilder
public abstract class C4Element {

    private final SecondaryElementType secondaryElementType;

    @Getter
    private final String alias;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final boolean external;
    @Builder.Default
    private final Set<String> technologies = new HashSet<>();
    @Builder.Default
    private final Set<String> stereotypes = new HashSet<>();
    private Map<String, String> properties;
    @Getter
    private C4Element parent;

    public String getProperties() {
        if (this.properties != null && this.properties.size() > 0) {
            return ", " + properties.entrySet()
                    .stream()
                    .map(e -> e.getKey() + ": \"" + e.getValue() + "\"")
                    .collect(Collectors.joining(", "));
        } else {
            return "";
        }
    }

    public String getTechnologies() {
        return "[" + this.technologies.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")) + "]";
    }

    public String getLabels() {
        Set<String> labels = new LinkedHashSet<>();
        labels.add("C4");
        labels.add(getPrimaryElementTypeLabel());
        this.secondaryElementType.getLabel().ifPresent(labels::add);
        labels.addAll(this.stereotypes);
        return String.join(":", labels);
    }

    abstract String getPrimaryElementTypeLabel();

}
