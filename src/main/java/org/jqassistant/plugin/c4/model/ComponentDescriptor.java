package org.jqassistant.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import org.jqassistant.plugin.c4.report.C4Language;

/**
 * Descriptor for a C4 Component.
 *
 * @author Stephan Pirnbaum
 */
@C4Language(C4Language.C4LanguageElement.Component)
@Label("Component")
public interface ComponentDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
