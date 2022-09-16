package org.jqassistant.contrib.plugin.c4;

import lombok.Getter;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;
import org.jqassistant.contrib.plugin.c4.data.*;
import org.jqassistant.contrib.plugin.c4.antlr.C4BaseListener;
import org.jqassistant.contrib.plugin.c4.antlr.C4Parser;
import org.jqassistant.contrib.plugin.c4.data.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

/**
 * Parse listener to hook into Antlr parsing.
 *
 * @author Stephan Pirnbaum
 */
public class C4ParseListener extends C4BaseListener {

    @Getter
    private String name;
    @Getter
    private final List<AbstractElement> c4Elements = new ArrayList<>();
    @Getter
    private final List<ElementRelation> c4ElementRelations = new ArrayList<>();

    private final ArrayList<AbstractElement> parentHierarchy = new ArrayList<>();
    private Map<String, String> properties = new LinkedHashMap<>();

    @Override
    public void exitC4(C4Parser.C4Context ctx) {
        this.name = normalize(ctx.name);
    }

    @Override
    public void enterHierarchy(C4Parser.HierarchyContext ctx) {
        parentHierarchy.add(c4Elements.get(c4Elements.size() - 1));
    }

    @Override
    public void exitHierarchy(C4Parser.HierarchyContext ctx) {
        parentHierarchy.remove(parentHierarchy.size() - 1);
    }

    @Override
    public void exitAddProperty(C4Parser.AddPropertyContext ctx) {
        properties.put(normalize(ctx.key), normalize(ctx.value));
    }

    @Override
    public void exitComponent(C4Parser.ComponentContext ctx) {
        Component.ComponentBuilder<?, ?> builder = Component.builder()
                .secondaryElementType(ctx.secondaryType)
                .external(ctx.external)
                .properties(properties);

        if (parentHierarchy.size() > 0) {
            builder.parent(parentHierarchy.get(parentHierarchy.size() - 1));
        }
        AbstractBuildingBlock element = processParameters(builder, ctx.paramList());

        c4Elements.add(element);
        properties = new HashMap<>();
    }

    @Override
    public void exitContainer(C4Parser.ContainerContext ctx) {
        Container.ContainerBuilder<?, ?> builder = Container.builder()
                .secondaryElementType(ctx.secondaryType)
                .external(ctx.external)
                .properties(properties);

        if (parentHierarchy.size() > 0) {
            builder.parent(parentHierarchy.get(parentHierarchy.size() - 1));
        }

        AbstractBuildingBlock element = processParameters(builder, ctx.paramList());

        c4Elements.add(element);
        properties = new HashMap<>();
    }

    @Override
    public void exitSystem(C4Parser.SystemContext ctx) {
        System.SystemBuilder<?, ?> builder = System.builder()
                .secondaryElementType(ctx.secondaryType)
                .external(ctx.external)
                .properties(properties);

        if (parentHierarchy.size() > 0) {
            builder.parent(parentHierarchy.get(parentHierarchy.size() - 1));
        }

        AbstractBuildingBlock element = processParameters(builder, ctx.systemParamList());

        c4Elements.add(element);
        properties = new HashMap<>();
    }

    @Override
    public void exitPerson(C4Parser.PersonContext ctx) {
        Person.PersonBuilder<?, ?> builder = Person.builder()
                .external(ctx.external)
                .properties(properties);

        if (parentHierarchy.size() > 0) {
            builder.parent(parentHierarchy.get(parentHierarchy.size() - 1));
        }

        AbstractBuildingBlock element = processParameters(builder, ctx.systemParamList());

        c4Elements.add(element);
        properties = new HashMap<>();
    }

    @Override
    public void exitBoundary(C4Parser.BoundaryContext ctx) {
        Boundary.BoundaryBuilder<?, ?> builder;

        if (StringUtils.isNotEmpty(ctx.type)) {
            builder = Boundary.builder()
                    .type(ctx.type);
            processParameters(builder, ctx.boundaryParamList());
        } else {
            builder = Boundary.builder();
            processParameters(builder, ctx.genericBoundaryParamList());
        }

        c4Elements.add(builder.build());
    }

    private void processParameters(Boundary.BoundaryBuilder<?, ?> builder, C4Parser.BoundaryParamListContext boundaryParamList) {
        if (boundaryParamList.p1 != null && StringUtils.isNotEmpty(boundaryParamList.p1.getText())) { // potentially alias
            String p = boundaryParamList.p1.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.alias(normalize(p));
            }
        }
        if (boundaryParamList.p2 != null && StringUtils.isNotEmpty(boundaryParamList.p2.getText())) { // potentially label
            String p = boundaryParamList.p2.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.name(normalize(p));
            }
        }
        if (boundaryParamList.p3 != null && StringUtils.isNotEmpty(boundaryParamList.p3.getText())) { // potentially tags
            String p = normalize(boundaryParamList.p3.getText());
            if (!setSpecifiedProperty(p, builder)) {
                processSet("", p, builder::stereotypes);
            }
        }
        if (boundaryParamList.p4 != null && StringUtils.isNotEmpty(boundaryParamList.p4.getText())) { // potentially link
            String p = normalize(boundaryParamList.p4.getText());
            setSpecifiedProperty(p, builder);
        }

    }

    private void processParameters(Boundary.BoundaryBuilder<?, ?> builder, C4Parser.GenericBoundaryParamListContext genericBoundaryParamList) {
        if (genericBoundaryParamList.p1 != null && StringUtils.isNotEmpty(genericBoundaryParamList.p1.getText())) { // potentially alias
            String p = genericBoundaryParamList.p1.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.alias(normalize(p));
            }
        }
        if (genericBoundaryParamList.p2 != null && StringUtils.isNotEmpty(genericBoundaryParamList.p2.getText())) { // potentially label
            String p = genericBoundaryParamList.p2.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.name(normalize(p));
            }
        }
        if (genericBoundaryParamList.p3 != null && StringUtils.isNotEmpty(genericBoundaryParamList.p3.getText())) { // potentially type
            String p = normalize(genericBoundaryParamList.p3.getText());
            if (!setSpecifiedProperty(p, builder)) {
                builder.type(p);
            }
        }
        if (genericBoundaryParamList.p4 != null && StringUtils.isNotEmpty(genericBoundaryParamList.p4.getText())) { // potentially tags
            String p = normalize(genericBoundaryParamList.p4.getText());
            if (!setSpecifiedProperty(p, builder)) {
                processSet("", p, builder::stereotypes);
            }
        }
        if (genericBoundaryParamList.p5 != null && StringUtils.isNotEmpty(genericBoundaryParamList.p5.getText())) { // potentially link
            String p = normalize(genericBoundaryParamList.p5.getText());
            setSpecifiedProperty(p, builder);
        }

    }

    private AbstractBuildingBlock processParameters(AbstractBuildingBlock.AbstractBuildingBlockBuilder<?, ?> builder, C4Parser.ParamListContext paramListContext) {
        if (paramListContext.p1 != null && StringUtils.isNotEmpty(paramListContext.p1.getText())) { // potentially alias
            String p = paramListContext.p1.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.alias(normalize(p));
            }
        }
        if (paramListContext.p2 != null && StringUtils.isNotEmpty(paramListContext.p2.getText())) { // potentially label
            String p = paramListContext.p2.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.name(normalize(p));
            }
        }
        if (paramListContext.p3 != null && StringUtils.isNotEmpty(paramListContext.p3.getText())) { // potentially tech
            String p = paramListContext.p3.getText();
            if (!setSpecifiedProperty(p, builder)) {
                processSet("", p, builder::technologies);
            }
        }
        if (paramListContext.p4 != null && StringUtils.isNotEmpty(paramListContext.p4.getText())) { // potentially description
            String p = paramListContext.p4.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.description(normalize(p));
            }
        }
        if (paramListContext.p5 != null && StringUtils.isNotEmpty(paramListContext.p5.getText())) { // potentially sprite
            String p = paramListContext.p5.getText();
            setSpecifiedProperty(p, builder);
        }
        if (paramListContext.p6 != null && StringUtils.isNotEmpty(paramListContext.p6.getText())) { // potentially tags
            String p = paramListContext.p6.getText();
            if (!setSpecifiedProperty(p, builder)) {
                processSet("", p, builder::stereotypes);
            }
        }
        if (paramListContext.p7 != null && StringUtils.isNotEmpty(paramListContext.p7.getText())) { // potentially links
            String p = paramListContext.p7.getText();
            setSpecifiedProperty(p, builder);
        }
        return builder.build();
    }

    private AbstractBuildingBlock processParameters(AbstractBuildingBlock.AbstractBuildingBlockBuilder<?, ?> builder, C4Parser.SystemParamListContext paramListContext) {
        if (paramListContext.p1 != null && StringUtils.isNotEmpty(paramListContext.p1.getText())) { // potentially alias
            String p = paramListContext.p1.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.alias(normalize(p));
            }
        }
        if (paramListContext.p2 != null && StringUtils.isNotEmpty(paramListContext.p2.getText())) { // potentially label
            String p = paramListContext.p2.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.name(normalize(p));
            }
        }
        if (paramListContext.p3 != null && StringUtils.isNotEmpty(paramListContext.p3.getText())) { // potentially description
            String p = paramListContext.p3.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.description(normalize(p));
            }
        }
        if (paramListContext.p4 != null && StringUtils.isNotEmpty(paramListContext.p4.getText())) { // potentially sprite
            String p = paramListContext.p4.getText();
            setSpecifiedProperty(p, builder);
        }
        if (paramListContext.p5 != null && StringUtils.isNotEmpty(paramListContext.p5.getText())) { // potentially tags
            String p = paramListContext.p5.getText();
            if (!setSpecifiedProperty(p, builder)) {
                processSet("", p, builder::stereotypes);
            }
        }
        if (paramListContext.p6 != null && StringUtils.isNotEmpty(paramListContext.p6.getText())) { // potentially links
            String p = paramListContext.p6.getText();
            setSpecifiedProperty(p, builder);
        }
        return builder.build();
    }

    @Override
    public void exitRel(C4Parser.RelContext ctx) {
        ElementRelation.ElementRelationBuilder builder = ElementRelation.builder()
                .properties(properties);

        ElementRelation relation = processParameters(builder, ctx.relParamList());

        c4ElementRelations.add(relation);
        properties = new HashMap<>();
    }

    @Override
    public void exitBiRel(C4Parser.BiRelContext ctx) {
        ElementRelation.ElementRelationBuilder builder1 = ElementRelation.builder()
                .properties(properties);

        ElementRelation relation1 = processParameters(builder1, ctx.relParamList());
        c4ElementRelations.add(relation1);

        ElementRelation relation2 = relation1.toBuilder()
                .from(relation1.getTo())
                .to(relation1.getFrom())
                .build();
        c4ElementRelations.add(relation2);
        properties = new HashMap<>();
    }

    private ElementRelation processParameters(ElementRelation.ElementRelationBuilder builder, C4Parser.RelParamListContext paramListContext) {
        if (paramListContext.p1 != null && StringUtils.isNotEmpty(paramListContext.p1.getText())) { // potentially from
            String p = paramListContext.p1.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.from(normalize(p));
            }
        }
        if (paramListContext.p2 != null && StringUtils.isNotEmpty(paramListContext.p2.getText())) { // potentially to
            String p = paramListContext.p2.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.to(normalize(p));
            }
        }
        if (paramListContext.p3 != null && StringUtils.isNotEmpty(paramListContext.p3.getText())) { // potentially label
            String p = paramListContext.p3.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.name(normalize(p));
            }
        }
        if (paramListContext.p4 != null && StringUtils.isNotEmpty(paramListContext.p4.getText())) { // potentially technology
            String p = paramListContext.p4.getText();
            if (!setSpecifiedProperty(p, builder)) {
                processSet("", p, builder::technologies);
            }
        }
        if (paramListContext.p5 != null && StringUtils.isNotEmpty(paramListContext.p5.getText())) { // potentially description
            String p = paramListContext.p5.getText();
            if (!setSpecifiedProperty(p, builder)) {
                builder.description(normalize(p));
            }
        }
        if (paramListContext.p6 != null && StringUtils.isNotEmpty(paramListContext.p6.getText())) { // potentially sprite
            String p = paramListContext.p6.getText();
            setSpecifiedProperty(p, builder);
        }
        if (paramListContext.p7 != null && StringUtils.isNotEmpty(paramListContext.p7.getText())) { // potentially tags
            String p = paramListContext.p7.getText();
            if (!setSpecifiedProperty(p, builder)) {
                processSet("", p, builder::stereotypes);
            }
        }
        if (paramListContext.p8 != null && StringUtils.isNotEmpty(paramListContext.p8.getText())) { // potentially links
            String p = paramListContext.p8.getText();
            setSpecifiedProperty(p, builder);
        }
        return builder.build();
    }

    private boolean setSpecifiedProperty(String value, Boundary.BoundaryBuilder<?, ?> builder) {
        return processAlias(value, builder::alias) ||
                processLabel(value, builder::name) ||
                processTags(value, builder::stereotypes) ||
                processType(value, builder::type) ||
                value.contains("$link");
    }

    private boolean setSpecifiedProperty(String value, AbstractBuildingBlock.AbstractBuildingBlockBuilder<?, ?> builder) {
        return processAlias(value, builder::alias) ||
                processLabel(value, builder::name) ||
                processTech(value, builder::technologies) ||
                processTags(value, builder::stereotypes) ||
                processDescription(value, builder::description) ||
                value.contains("$sprite") ||
                value.contains("$link");
    }

    private boolean setSpecifiedProperty(String value, ElementRelation.ElementRelationBuilder builder) {
        return processFrom(value, builder::from) ||
                processTo(value, builder::to) ||
                processLabel(value, builder::name) ||
                processTags(value, builder::stereotypes) ||
                processDescription(value, builder::description) ||
                value.contains("$sprite") ||
                value.contains("$link");
    }

    private boolean processAlias(String value, Consumer<String> setter) {
        return process("$alias", value, setter);
    }

    private boolean processLabel(String value, Consumer<String> setter) {
        return process("$label", value, setter);
    }

    private boolean processDescription(String value, Consumer<String> setter) {
        return process("$descr", value, setter);
    }

    private boolean processTags(String value, Consumer<Set<String>> setter) {
        return processSet("$tags", value, setter);
    }

    private boolean processTech(String value, Consumer<Set<String>> setter) {
        return processSet("$techn", value, setter);
    }

    private boolean processType(String value, Consumer<String> setter) {
        return process("$type", value, setter);
    }

    private boolean processFrom(String value, Consumer<String> setter) {
        return process("$from", value, setter);
    }

    private boolean processTo(String value, Consumer<String> setter) {
        return process("$to", value, setter);
    }

    private boolean process(String ident, String value, Consumer<String> setter) {
        if (value.contains(ident)) {
            setter.accept(normalize(value.replace(ident, "").replaceFirst("=", "")));
            return true;
        }
        return false;
    }

    private boolean processSet(String ident, String value, Consumer<Set<String>> setter) {
        if (value.contains(ident)) {
            setter.accept(stream(value.replace(ident, "").replaceFirst("=", "").split("[,+]")).map(this::normalize).collect(toSet()));
            return true;
        }
        return false;
    }

    private String normalize(String text) {
        if (text == null) {
            return null;
        } else {
            return text.replace("\"", "").trim();
        }
    }

    private String normalize(Token token) {
        if (token == null || token.getText() == null) {
            return null;
        } else {
            return token.getText().replace("\"", "").trim();
        }
    }
}
