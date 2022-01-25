package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Base descriptor for all supported C4 elements (Component, Container, System).
 *
 * @author Stephan Pirnbaum
 */
public interface C4ElementDescriptor extends Descriptor {

    String getAlias();

    void setAlias(String alias);

    String getName();

    void setName(String name);

    String[] getTechnologies();

    void setTechnologies(String[] technologies);

    @Relation("CONTAINS")
    List<C4ComponentDescriptor> getContainedComponents();

    @Relation("CONTAINS")
    List<C4ContainerDescriptor> getContainedContainers();

    @Relation("CONTAINS")
    List<C4SystemDescriptor> getContainedSystems();

}
