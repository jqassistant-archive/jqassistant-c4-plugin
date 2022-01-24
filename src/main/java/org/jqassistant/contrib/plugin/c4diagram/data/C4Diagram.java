package org.jqassistant.contrib.plugin.c4diagram.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class C4Diagram {

    private final List<C4Element> elements;
    private final List<C4ElementRelation> relations;

}
