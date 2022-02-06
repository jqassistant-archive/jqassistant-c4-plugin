package org.jqassistant.contrib.plugin.c4.data;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a C4 building block, i.e. all elements except a {@link Boundary}.
 *
 * @author Stephan Pirnbaum
 */
@SuperBuilder
public abstract class AbstractBuildingBlock extends AbstractElement {

    private final String description;
    private final boolean external;
    private final SecondaryElementType secondaryElementType;
    @Builder.Default
    private final Set<String> technologies = new HashSet<>();
    private Map<String, String> properties;

    @Override
    public String buildStringRepresentation() {
        return String.format("%s{%s%s%s%s%s%s}",
                buildLabelString(), buildAliasString(), buildNameString(), buildDescriptionString(), buildTechnologiesString(), buildExternalString(), buildPropertiesString());
    }

    private String buildDescriptionString() {
        if (StringUtils.isNotEmpty(this.description)) {
            return ", description: \"" + this.description + "\"";
        } else {
            return "";
        }
    }

    private String buildExternalString() {
        return ", external: " + this.external;
    }

    private String buildPropertiesString() {
        if (this.properties != null && this.properties.size() > 0) {
            return ", " + properties.entrySet()
                    .stream()
                    .map(e -> e.getKey() + ": \"" + e.getValue() + "\"")
                    .collect(Collectors.joining(", "));
        } else {
            return "";
        }
    }

    private String buildTechnologiesString() {
        if (CollectionUtils.isNotEmpty(this.technologies)) {
            return ", technologies: [" + this.technologies.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")) + "]";

        } else {
            return "";
        }
    }

    @Override
    Set<String> getAdditionalStereotypes() {
        Set<String> labels = new LinkedHashSet<>();
        labels.add(getPrimaryElementTypeLabel());
        if (this.secondaryElementType != null) {
            this.secondaryElementType.getLabel().ifPresent(labels::add);
        }
        return labels;
    }

    abstract String getPrimaryElementTypeLabel();

}
