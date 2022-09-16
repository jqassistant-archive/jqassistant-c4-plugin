package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import org.jqassistant.contrib.plugin.c4.report.C4Language;

import static org.jqassistant.contrib.plugin.c4.report.C4Language.C4LanguageElement.Container;

/**
 * Descriptor for a C4 Container.
 *
 * @author Stephan Pirnbaum
 */
@C4Language(Container)
@Label("Container")
public interface ContainerDescriptor extends BuildingBlockDescriptor, C4Descriptor {

}
