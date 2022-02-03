package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for a C4 Container.
 *
 * @author Stephan Pirnbaum
 */
@Label("Container")
public interface ContainerDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
