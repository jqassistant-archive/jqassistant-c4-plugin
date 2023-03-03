package org.jqassistant.plugin.c4;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.xo.api.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.jqassistant.plugin.c4.data.AbstractElement;
import org.jqassistant.plugin.c4.data.Boundary;
import org.jqassistant.plugin.c4.data.C4Diagram;
import org.jqassistant.plugin.c4.data.Component;
import org.jqassistant.plugin.c4.data.Container;
import org.jqassistant.plugin.c4.data.ElementRelation;
import org.jqassistant.plugin.c4.data.Person;
import org.jqassistant.plugin.c4.data.System;
import org.jqassistant.plugin.c4.model.BoundaryDescriptor;
import org.jqassistant.plugin.c4.model.C4DiagramDescriptor;
import org.jqassistant.plugin.c4.model.ComponentDescriptor;
import org.jqassistant.plugin.c4.model.ContainerDescriptor;
import org.jqassistant.plugin.c4.model.ElementDescriptor;
import org.jqassistant.plugin.c4.model.PersonDescriptor;
import org.jqassistant.plugin.c4.model.SystemDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to persist a given {@link C4Diagram}.
 *
 * @author Stephan Pirnbaum
 */
@RequiredArgsConstructor
@Slf4j
public class C4DiagramPersister {

    private static final String relQueryTemplate = "MATCH (s:C4), (t:C4) WHERE id(s) = %d AND id(t) = %d MERGE (s)-[d%s]->(t) RETURN d";
    private static final String elementQueryTemplate = "CREATE (n%s) RETURN n";

    private final Store store;

    /**
     * Persists the given {@link C4Diagram}.
     *
     * @param c4Diagram The diagram to persist.
     * @return The created {@link C4DiagramDescriptor}.
     */
    public C4DiagramDescriptor persist(C4Diagram c4Diagram) {
        C4DiagramDescriptor c4DiagramDescriptor = store.create(C4DiagramDescriptor.class);
        c4DiagramDescriptor.setName(c4Diagram.getName());
        Map<String, ElementDescriptor> elementAliasMap = persistElements(c4DiagramDescriptor, c4Diagram.getElements());
        persistRelations(elementAliasMap, c4Diagram.getRelations());

        return c4DiagramDescriptor;
    }

    private Map<String, ElementDescriptor> persistElements(C4DiagramDescriptor c4Diagram, List<AbstractElement> elements) {
        Map<String, ElementDescriptor> elementAliasMap = new HashMap<>();
        // persist elements
        for (AbstractElement element : elements) {
            ElementDescriptor c4Element = createElement(c4Diagram, element);
            if (c4Element != null) {
                elementAliasMap.put(c4Element.getAlias(), c4Element);
            }
        }

        // persist parent-child relation
        for (AbstractElement element : elements) {
            if (element.getParent() != null) {
                ElementDescriptor parent = elementAliasMap.get(element.getParent().getAlias());
                ElementDescriptor child = elementAliasMap.get(element.getAlias());
                if (child instanceof ComponentDescriptor) {
                    parent.getContainedComponents().add((ComponentDescriptor) child);
                } else if (child instanceof ContainerDescriptor) {
                    parent.getContainedContainers().add((ContainerDescriptor) child);
                } else if (child instanceof SystemDescriptor) {
                    parent.getContainedSystems().add((SystemDescriptor) child);
                } else if (child instanceof PersonDescriptor) {
                    parent.getContainedPersons().add((PersonDescriptor) child);
                } else if (child instanceof BoundaryDescriptor) {
                    parent.getContainedBoundaries().add((BoundaryDescriptor) child);
                }
            }
        }
        return elementAliasMap;
    }

    private void persistRelations(Map<String, ElementDescriptor> elementAliasMap, List<ElementRelation> relations) {
        for (ElementRelation relation : relations) {
            ElementDescriptor from = elementAliasMap.get(relation.getFrom());
            ElementDescriptor to = elementAliasMap.get(relation.getTo());
            if (from != null && to != null) {
                String query = String.format(relQueryTemplate, (Long) from.getId(), (Long) to.getId(), relation.buildStringRepresentation(from.getAlias(), from.getId(), to.getAlias(), to.getId()));
                this.store.executeQuery(query);
            }
        }
    }

    private ElementDescriptor createElement(C4DiagramDescriptor diagram, AbstractElement element) {
        String query = String.format(elementQueryTemplate, element.buildStringRepresentation());

        Query.Result<Query.Result.CompositeRowObject> result = this.store.executeQuery(query);
        List<Query.Result.CompositeRowObject> resultList = IteratorUtils.toList(result.iterator());
        if (resultList.size() == 1) {
            if (element instanceof Component) {
                ComponentDescriptor c = resultList.get(0).get("n", ComponentDescriptor.class);
                diagram.getComponents().add(c);
                return c;
            } else if (element instanceof Container) {
                ContainerDescriptor c = resultList.get(0).get("n", ContainerDescriptor.class);
                diagram.getContainers().add(c);
                return c;
            } else if (element instanceof System) {
                SystemDescriptor s = resultList.get(0).get("n", SystemDescriptor.class);
                diagram.getSystems().add(s);
                return s;
            } else if (element instanceof Person) {
                PersonDescriptor p = resultList.get(0).get("n", PersonDescriptor.class);
                diagram.getPersons().add(p);
                return p;
            } else if (element instanceof Boundary) {
                BoundaryDescriptor b = resultList.get(0).get("n", BoundaryDescriptor.class);
                diagram.getBoundaries().add(b);
                return b;
            }
        }

        return null;
    }

}
