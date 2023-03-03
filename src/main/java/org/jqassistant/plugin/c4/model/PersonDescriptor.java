package org.jqassistant.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import org.jqassistant.plugin.c4.report.C4Language;

/**
 * Descriptor for a C4 Person.
 *
 * @author Stephan Pirnbaum
 */
@C4Language(C4Language.C4LanguageElement.Person)
@Label("Person")
public interface PersonDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
