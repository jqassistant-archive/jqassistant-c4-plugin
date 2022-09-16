package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import org.jqassistant.contrib.plugin.c4.report.C4Language;

import static org.jqassistant.contrib.plugin.c4.report.C4Language.C4LanguageElement.Boundary;

/**
 * Descriptor for a C4 Boundary.
 *
 * @author Stephan Pirnbaum
 */
@C4Language(Boundary)
@Label("Boundary")
public interface BoundaryDescriptor extends ElementDescriptor {
}
