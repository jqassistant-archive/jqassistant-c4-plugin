package org.jqassistant.contrib.plugin.c4.data;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
public class C4ElementRelation {

    private final String from;
    private final String to;
    private final String name;
    private final String description;
    @Builder.Default
    private final Set<String> technologies = new LinkedHashSet<>();
    @Builder.Default
    private final Set<String> stereotypes = new LinkedHashSet<>();
    private final Map<String, String> properties;

    public String getDescription() {
        if (StringUtils.isEmpty(description)) {
            return "";
        } else {
            return ", description: \"" + description + "\"";
        }
    }

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
        return ", technologies: [" + this.technologies.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")) + "]";
    }

}
