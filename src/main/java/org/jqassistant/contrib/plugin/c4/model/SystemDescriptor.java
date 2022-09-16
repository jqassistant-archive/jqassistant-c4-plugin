package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import org.jqassistant.contrib.plugin.c4.report.C4Language;

import static org.jqassistant.contrib.plugin.c4.report.C4Language.C4LanguageElement.System;

/**
 * Descriptor for a C4 System.
 *
 * @author Stephan Pirnbaum
 */
@C4Language(System)
@Label("System")
public interface SystemDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
