package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import org.jqassistant.contrib.plugin.c4.report.C4Language;

import static org.jqassistant.contrib.plugin.c4.report.C4Language.C4LanguageElement.Component;

/**
 * Descriptor for a C4 Component.
 *
 * @author Stephan Pirnbaum
 */
@C4Language(Component)
@Label("Component")
public interface ComponentDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
