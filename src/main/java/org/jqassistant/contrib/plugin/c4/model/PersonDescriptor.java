package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import org.jqassistant.contrib.plugin.c4.report.C4Language;

import static org.jqassistant.contrib.plugin.c4.report.C4Language.C4LanguageElement.Person;

/**
 * Descriptor for a C4 Person.
 *
 * @author Stephan Pirnbaum
 */
@C4Language(Person)
@Label("Person")
public interface PersonDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
