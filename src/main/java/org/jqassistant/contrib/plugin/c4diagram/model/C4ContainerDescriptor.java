package org.jqassistant.contrib.plugin.c4diagram.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for a C4 Container.
 *
 * @author Stephan Pirnbaum
 */
@Label("Container")
public interface C4ContainerDescriptor extends C4ElementDescriptor, C4Descriptor {

}
