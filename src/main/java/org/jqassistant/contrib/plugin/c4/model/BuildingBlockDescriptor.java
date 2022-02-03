package org.jqassistant.contrib.plugin.c4.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

/**
 * Base descriptor for all supported C4 elements (Component, Container, System, Person).
 *
 * @author Stephan Pirnbaum
 */
public interface BuildingBlockDescriptor extends ElementDescriptor {

    void setDescription(String description);

    String getDescription();

    void setExternal(boolean external);

    boolean getExternal();

    String[] getTechnologies();

    void setTechnologies(String[] technologies);

}
