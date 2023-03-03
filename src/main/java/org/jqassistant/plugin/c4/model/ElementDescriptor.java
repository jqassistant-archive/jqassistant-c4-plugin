package org.jqassistant.plugin.c4.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Base descriptor for all C4 elements (building blocks, boundaries).
 *
 * @author Stephan Pirnbaum
 */
public interface ElementDescriptor extends Descriptor {

    String getAlias();

    void setAlias(String alias);

    String getName();

    void setName(String name);

    @Relation("CONTAINS")
    List<ComponentDescriptor> getContainedComponents();

    @Relation("CONTAINS")
    List<ContainerDescriptor> getContainedContainers();

    @Relation("CONTAINS")
    List<SystemDescriptor> getContainedSystems();

    @Relation("CONTAINS")
    List<PersonDescriptor> getContainedPersons();

    @Relation("CONTAINS")
    List<BoundaryDescriptor> getContainedBoundaries();

}
