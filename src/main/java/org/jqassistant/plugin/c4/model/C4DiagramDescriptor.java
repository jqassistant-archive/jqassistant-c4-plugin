package org.jqassistant.plugin.c4.model;

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

    void setName(String name);

    String getName();

    @Relation("HAS")
    List<ComponentDescriptor> getComponents();

    @Relation("HAS")
    List<ContainerDescriptor> getContainers();

    @Relation("HAS")
    List<SystemDescriptor> getSystems();

    @Relation("HAS")
    List<PersonDescriptor> getPersons();

    @Relation("HAS")
    List<BoundaryDescriptor> getBoundaries();

}
