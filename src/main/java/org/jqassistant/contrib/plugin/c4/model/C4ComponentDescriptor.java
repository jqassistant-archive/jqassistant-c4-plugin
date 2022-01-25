package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for a C4 Component.
 *
 * @author Stephan Pirnbaum
 */
@Label("Component")
public interface C4ComponentDescriptor extends C4ElementDescriptor, C4Descriptor {

}
