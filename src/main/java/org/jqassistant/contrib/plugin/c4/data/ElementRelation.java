package org.jqassistant.contrib.plugin.c4.data;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a relation between C4 elements.
 *
 * @author Stephan Pirnbaum
 */
@Builder
@Slf4j
public class ElementRelation {

    @Getter
    private final String from;
    @Getter
    private final String to;
    private final String name;
    private final String description;
    @Builder.Default
    private final Set<String> technologies = new LinkedHashSet<>();
    @Builder.Default
    private final Set<String> stereotypes = new LinkedHashSet<>();
    private final Map<String, String> properties;

    public String buildStringRepresentation(String sourceName, Long sourceNode, String targetName, Long targetNode) {
        String label;
        if (stereotypes.size() > 1) {
            label = stereotypes.stream().findFirst().get();
            log.warn("Relation between {} and {} has more then one stereotype. Using {}", sourceName, targetName, label);
        } else if (stereotypes.size() == 1) {
            label = stereotypes.stream().findFirst().get();
        } else {
            label = "DEPENDS_ON";
            log.warn("Relation between {} and {} has has no stereotypes. Using default {}", sourceName, targetName, label);
        }

        return String.format(":%s{%s%s%s%s}", label, buildNameString(), buildDescriptionString(), buildTechnologiesString(), buildPropertiesString());
    }

    private String buildNameString() {
        return "name: \"" + this.name + "\"";
    }


    private String buildDescriptionString() {
        if (StringUtils.isEmpty(description)) {
            return "";
        } else {
            return ", description: \"" + description + "\"";
        }
    }

    private String buildPropertiesString() {
        if (MapUtils.isEmpty(this.properties)) {
            return "";
        } else {
            return ", " + properties.entrySet()
                    .stream()
                    .map(e -> e.getKey() + ": \"" + e.getValue() + "\"")
                    .collect(Collectors.joining(", "));
        }
    }

    private String buildTechnologiesString() {
        if (CollectionUtils.isEmpty(this.technologies)) {
            return "";
        } else {
            return ", technologies: [" + this.technologies.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")) + "]";
        }
    }

}
