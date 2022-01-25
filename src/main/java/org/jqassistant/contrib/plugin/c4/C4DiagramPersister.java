package org.jqassistant.contrib.plugin.c4;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.xo.api.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.jqassistant.contrib.plugin.c4.data.*;
import org.jqassistant.contrib.plugin.c4.model.*;
import org.jqassistant.contrib.plugin.c4.data.*;
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
    private static final String elementQueryTemplate = "CREATE (n:%s{alias: \"%s\", name: \"%s\", description: \"%s\", technologies: %s, external: %b%s}) RETURN n";

    private final Store store;

    public C4DiagramDescriptor persist(C4Diagram c4Diagram) {
        C4DiagramDescriptor c4DiagramDescriptor = store.create(C4DiagramDescriptor.class);

        Map<String, C4ElementDescriptor> elementAliasMap = persistElements(c4DiagramDescriptor, c4Diagram.getElements());
        persistRelations(elementAliasMap, c4Diagram.getRelations());

        return c4DiagramDescriptor;
    }

    private Map<String, C4ElementDescriptor> persistElements(C4DiagramDescriptor c4Diagram, List<C4Element> elements) {
        Map<String, C4ElementDescriptor> elementAliasMap = new HashMap<>();
        // persist elements
        for (C4Element element : elements) {
            C4ElementDescriptor c4Element = createElement(c4Diagram, element);
            if (c4Element != null) {
                elementAliasMap.put(c4Element.getAlias(), c4Element);
            }
        }

        // persist parent-child relation
        for (C4Element element : elements) {
            if (element.getParent() != null) {
                C4ElementDescriptor parent = elementAliasMap.get(element.getParent().getAlias());
                C4ElementDescriptor child = elementAliasMap.get(element.getAlias());
                if (child instanceof C4ComponentDescriptor) {
                    parent.getContainedComponents().add((C4ComponentDescriptor) child);
                } else if (child instanceof C4ContainerDescriptor) {
                    parent.getContainedContainers().add((C4ContainerDescriptor) child);
                } else if (child instanceof C4SystemDescriptor) {
                    parent.getContainedSystems().add((C4SystemDescriptor) child);
                }
            }
        }
        return elementAliasMap;
    }

    private void persistRelations(Map<String, C4ElementDescriptor> elementAliasMap, List<C4ElementRelation> relations) {
        for (C4ElementRelation relation : relations) {
            C4ElementDescriptor from = elementAliasMap.get(relation.getFrom());
            C4ElementDescriptor to = elementAliasMap.get(relation.getTo());
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

    public C4ElementDescriptor createElement(C4DiagramDescriptor diagram, C4Element element) {
        String query = String.format(elementQueryTemplate,
                element.getLabels(), element.getAlias(), element.getName(), element.getDescription(), element.getTechnologies(), element.isExternal(), element.getProperties());

        Query.Result<Query.Result.CompositeRowObject> result = this.store.executeQuery(query);
        List<Query.Result.CompositeRowObject> resultList = IteratorUtils.toList(result.iterator());
        if (resultList.size() == 1) {
            if (element instanceof C4Component) {
                C4ComponentDescriptor c = resultList.get(0).get("n", C4ComponentDescriptor.class);
                diagram.getComponents().add(c);
                return c;
            } else if (element instanceof C4Container) {
                C4ContainerDescriptor c = resultList.get(0).get("n", C4ContainerDescriptor.class);
                diagram.getContainers().add(c);
                return c;
            } else if (element instanceof C4System) {
                C4SystemDescriptor s = resultList.get(0).get("n", C4SystemDescriptor.class);
                diagram.getSystems().add(s);
                return s;
            }
        }

        return null;
    }

}
