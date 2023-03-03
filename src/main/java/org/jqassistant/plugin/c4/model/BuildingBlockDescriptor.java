package org.jqassistant.plugin.c4.model;

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
