package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Base descriptor for all C4 related types.
 *
 * @author Stephan Pirnbaum
 */
@Label("C4")
public interface C4Descriptor extends Descriptor {
}
