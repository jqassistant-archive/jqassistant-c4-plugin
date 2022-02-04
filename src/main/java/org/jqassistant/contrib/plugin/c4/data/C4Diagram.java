package org.jqassistant.contrib.plugin.c4.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class C4Diagram {

    private final String name;
    private final List<AbstractElement> elements;
    private final List<ElementRelation> relations;

}
