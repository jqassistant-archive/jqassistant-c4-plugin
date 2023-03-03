package org.jqassistant.plugin.c4.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Representation of a C4 diagram.
 *
 * @author Stephan Pirnbaum
 */
@RequiredArgsConstructor
@Getter
public class C4Diagram {

    private final String name;
    private final List<AbstractElement> elements;
    private final List<ElementRelation> relations;

}
