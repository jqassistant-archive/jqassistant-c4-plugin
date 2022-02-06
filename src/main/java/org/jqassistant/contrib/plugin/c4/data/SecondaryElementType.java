package org.jqassistant.contrib.plugin.c4.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Enum for the secondary element type.
 *
 * @author Stephan Pirnbaum
 */
@RequiredArgsConstructor
@Getter
public enum SecondaryElementType {

    DEFAULT(Optional.empty()),
    DB(Optional.of("DB")),
    QUEUE(Optional.of("Queue"));

    private final Optional<String> label;

}
