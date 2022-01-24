package org.jqassistant.contrib.plugin.c4diagram.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Descriptor representing a C4 Diagram.
 *
 * @author Stephan Pirnbaum
 */
@Label("Diagram")
public interface C4DiagramDescriptor extends C4Descriptor {

    @Relation("HAS")
    List<C4ComponentDescriptor> getComponents();

    @Relation("HAS")
    List<C4ContainerDescriptor> getContainers();

    @Relation("HAS")
    List<C4SystemDescriptor> getSystems();

}
