package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for a C4 Person.
 *
 * @author Stephan Pirnbaum
 */
@Label("Person")
public interface PersonDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
