package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for a C4 System.
 *
 * @author Stephan Pirnbaum
 */
@Label("System")
public interface SystemDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
