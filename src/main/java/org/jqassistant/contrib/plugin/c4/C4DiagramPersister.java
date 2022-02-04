package org.jqassistant.contrib.plugin.c4;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.xo.api.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.jqassistant.contrib.plugin.c4.data.*;
import org.jqassistant.contrib.plugin.c4.data.System;
import org.jqassistant.contrib.plugin.c4.model.*;

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

    private static final String relQueryTemplate = "MATCH (s:C4), (t:C4) WHERE id(s) = %d AND id(t) = %d MERGE (s)-[d:%s{name: \"%s\"%s%s%s}]->(t) RETURN d";
    private static final String elementQueryTemplate = "CREATE (n%s) RETURN n";

    private final Store store;

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
                String label;
                if (relation.getStereotypes().size() > 1) {
                    label = relation.getStereotypes().stream().findFirst().get();
                    log.warn("Relation between {} and {} has more then one stereotype. Using {}", from.getAlias(), to.getAlias(), label);
                } else if (relation.getStereotypes().size() == 1) {
                    label = relation.getStereotypes().stream().findFirst().get();
                } else {
                    label = "DEPENDS_ON";
                    log.warn("Relation between {} and {} has has no stereotypes. Using default {}", from.getAlias(), to.getAlias(), label);
                }
                String query = String.format(relQueryTemplate, from.getId(), to.getId(), label, relation.getName(), relation.getDescription(), relation.getTechnologies(), relation.getProperties());
                this.store.executeQuery(query);
            }
        }
    }

    public ElementDescriptor createElement(C4DiagramDescriptor diagram, AbstractElement element) {
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
