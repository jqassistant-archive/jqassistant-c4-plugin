package org.jqassistant.plugin.c4.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Descriptor representing a single C4 .puml-file.
 *
 * @author Stephan Pirnbaum
 */
public interface C4FileDescriptor extends C4Descriptor, FileDescriptor {

    @Relation("CONTAINS")
    C4DiagramDescriptor getDiagram();

    void setDiagram(C4DiagramDescriptor diagram);

}
