package org.jqassistant.contrib.plugin.c4;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;
import org.jqassistant.contrib.plugin.c4.data.*;
import org.jqassistant.contrib.plugin.c4.antlr.C4BaseListener;
import org.jqassistant.contrib.plugin.c4.antlr.C4Parser;
import org.jqassistant.contrib.plugin.c4.data.System;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

/**
 * Parse listener to hook into Antlr parsing.
 *
 * @author Stephan Pirnbaum
 */
@RequiredArgsConstructor
public class C4ParseListener extends C4BaseListener {

    @Getter
    private final List<AbstractElement> c4Elements;
    @Getter
    private final List<ElementRelation> c4ElementRelations;

    private final ArrayList<AbstractElement> parentHierarchy = new ArrayList<>();
    private Map<String, String> properties = new LinkedHashMap<>();

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
        Component.ComponentBuilder builder = Component.builder()
                .alias(normalize(ctx.paramList().alias))
                .name(normalize(ctx.paramList().label))
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
        Container.ContainerBuilder builder = Container.builder()
                .alias(normalize(ctx.paramList().alias))
                .name(normalize(ctx.paramList().label))
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
        System.SystemBuilder builder = System.builder()
                .alias(normalize(ctx.systemParamList().alias))
                .name(normalize(ctx.systemParamList().label))
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
        Person.PersonBuilder builder = Person.builder()
                .alias(normalize(ctx.systemParamList().alias))
                .name(normalize(ctx.systemParamList().label))
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
        Boundary.BoundaryBuilder builder = Boundary.builder()
                .alias(normalize(ctx.boundaryParamList().alias))
                .name(normalize(ctx.boundaryParamList().label));

        if (StringUtils.isNotEmpty(ctx.type)) {
            builder.type(ctx.type);
            processParameters(builder, ctx.boundaryParamList());
        } else {
            processParameters(builder, ctx.genericBoundaryParamList());
        }

        c4Elements.add(builder.build());
    }

    private void processParameters(Boundary.BoundaryBuilder elementBuilder, C4Parser.BoundaryParamListContext boundaryParamList) {
        // two optional paramters
        if (boundaryParamList.opt1 != null && !StringUtils.isEmpty(boundaryParamList.opt1.getText())) { // potentially tags
            String opt = boundaryParamList.opt1.getText();
            if (!setSpecifiedProperty(opt, elementBuilder)) {
                elementBuilder.stereotypes(stream(opt.split(",")).map(this::normalize).collect(toSet()));
            }
        }
        if (boundaryParamList.opt2 != null && !StringUtils.isEmpty(boundaryParamList.opt2.getText())) { // potentially link
            String opt = boundaryParamList.opt2.getText();
            setSpecifiedProperty(opt, elementBuilder);
        }

    }

    private void processParameters(Boundary.BoundaryBuilder elementBuilder, C4Parser.GenericBoundaryParamListContext genericBoundaryParamList) {
        // three optional parameters:
        if (genericBoundaryParamList.opt1 != null && !StringUtils.isEmpty(genericBoundaryParamList.opt1.getText())) { // potentially type
            String opt = genericBoundaryParamList.opt1.getText();
            if (!setSpecifiedProperty(opt, elementBuilder)) {
                elementBuilder.type(opt);
            }
        }
        if (genericBoundaryParamList.opt2 != null && !StringUtils.isEmpty(genericBoundaryParamList.opt2.getText())) { // potentially tags
            String opt = genericBoundaryParamList.opt2.getText();
            if (!setSpecifiedProperty(opt, elementBuilder)) {
                elementBuilder.stereotypes(stream(opt.split(",")).map(this::normalize).collect(toSet()));
            }
        }
        if (genericBoundaryParamList.opt3 != null && !StringUtils.isEmpty(genericBoundaryParamList.opt3.getText())) { // potentially link
            String opt = genericBoundaryParamList.opt3.getText();
            setSpecifiedProperty(opt, elementBuilder);
        }

    }

    private AbstractBuildingBlock processParameters(AbstractBuildingBlock.AbstractBuildingBlockBuilder builder, C4Parser.ParamListContext paramListContext) {
        if (paramListContext.tech != null && !StringUtils.isEmpty(paramListContext.tech.getText())) {
            builder.technologies(stream(paramListContext.tech.getText().split(",")).map(this::normalize).collect(toSet()));
        }
        if (paramListContext.opt1 != null && !StringUtils.isEmpty(paramListContext.opt1.getText())) { // potentially description
            String opt = paramListContext.opt1.getText();
            if (!setSpecifiedProperty(opt, builder)) {
                builder.description(normalize(opt));
            }
        }
        if (paramListContext.opt2 != null && !StringUtils.isEmpty(paramListContext.opt2.getText())) { // potentially sprite
            String opt = paramListContext.opt2.getText();
            setSpecifiedProperty(opt, builder);
        }
        if (paramListContext.opt3 != null && !StringUtils.isEmpty(paramListContext.opt3.getText())) { // potentially tags
            String opt = paramListContext.opt3.getText();
            if (!setSpecifiedProperty(opt, builder)) {
                builder.stereotypes(stream(opt.split(",")).map(this::normalize).collect(toSet()));
            }
        }
        if (paramListContext.opt4 != null && !StringUtils.isEmpty(paramListContext.opt4.getText())) { // potentially links
            String opt = paramListContext.opt4.getText();
            setSpecifiedProperty(opt, builder);
        }
        return builder.build();
    }

    private AbstractBuildingBlock processParameters(AbstractBuildingBlock.AbstractBuildingBlockBuilder builder, C4Parser.SystemParamListContext paramListContext) {
        if (paramListContext.opt1 != null && !StringUtils.isEmpty(paramListContext.opt1.getText())) { // potentially description
            String opt = paramListContext.opt1.getText();
            if (!setSpecifiedProperty(opt, builder)) {
                builder.description(normalize(opt));
            }
        }
        if (paramListContext.opt2 != null && !StringUtils.isEmpty(paramListContext.opt2.getText())) { // potentially sprite
            String opt = paramListContext.opt2.getText();
            setSpecifiedProperty(opt, builder);
        }
        if (paramListContext.opt3 != null && !StringUtils.isEmpty(paramListContext.opt3.getText())) { // potentially tags
            String opt = paramListContext.opt3.getText();
            if (!setSpecifiedProperty(opt, builder)) {
                builder.stereotypes(stream(opt.split(",")).map(this::normalize).collect(toSet()));
            }
        }
        if (paramListContext.opt4 != null && !StringUtils.isEmpty(paramListContext.opt4.getText())) { // potentially links
            String opt = paramListContext.opt4.getText();
            setSpecifiedProperty(opt, builder);
        }
        return builder.build();
    }

    @Override
    public void exitRel(C4Parser.RelContext ctx) {
        ElementRelation.ElementRelationBuilder builder = ElementRelation.builder()
                .from(normalize(ctx.relParamList().from))
                .to(normalize(ctx.relParamList().to))
                .name(normalize(ctx.relParamList().label))
                .properties(properties);

        ElementRelation relation = processParameters(builder, ctx.relParamList());

        c4ElementRelations.add(relation);
        properties = new HashMap<>();
    }

    private ElementRelation processParameters(ElementRelation.ElementRelationBuilder builder, C4Parser.RelParamListContext paramListContext) {
        if (paramListContext.opt1 != null && !StringUtils.isEmpty(paramListContext.opt1.getText())) { // potentially technology
            String opt = paramListContext.opt1.getText();
            if (!setSpecifiedProperty(opt, builder)) {
                builder.technologies(stream(opt.split(",")).map(this::normalize).collect(toSet()));
            }
        }
        if (paramListContext.opt2 != null && !StringUtils.isEmpty(paramListContext.opt2.getText())) { // potentially description
            String opt = paramListContext.opt2.getText();
            if (!setSpecifiedProperty(opt, builder)) {
                builder.description(normalize(opt));
            }
        }
        if (paramListContext.opt3 != null && !StringUtils.isEmpty(paramListContext.opt3.getText())) { // potentially sprite
            String opt = paramListContext.opt3.getText();
            setSpecifiedProperty(opt, builder);
        }
        if (paramListContext.opt4 != null && !StringUtils.isEmpty(paramListContext.opt4.getText())) { // potentially tags
            String opt = paramListContext.opt4.getText();
            if (!setSpecifiedProperty(opt, builder)) {
                builder.stereotypes(stream(opt.split(",")).map(this::normalize).collect(toSet()));
            }
        }
        if (paramListContext.opt5 != null && !StringUtils.isEmpty(paramListContext.opt5.getText())) { // potentially links
            String opt = paramListContext.opt5.getText();
            setSpecifiedProperty(opt, builder);
        }
        return builder.build();
    }

    private boolean setSpecifiedProperty(String value, Boundary.BoundaryBuilder builder) {
        return processTags(value, builder::stereotypes) || processType(value, builder::type) || value.contains("$link");
    }

    private boolean setSpecifiedProperty(String value, AbstractBuildingBlock.AbstractBuildingBlockBuilder builder) {
        return processTags(value, builder::stereotypes) || processDescription(value, builder::description) || value.contains("$sprite") || value.contains("$link");
    }

    private boolean setSpecifiedProperty(String value, ElementRelation.ElementRelationBuilder builder) {
        return processTags(value, builder::stereotypes) || processDescription(value, builder::description) || value.contains("$sprite") || value.contains("$link");
    }

    private boolean processTags(String value, Consumer<Set<String>> setter) {
        if (value.contains("$tags")) {
            setter.accept(stream(value.replace("$tags", "").replaceFirst("=", "").split(",")).map(this::normalize).collect(toSet()));
            return true;
        }
        return false;
    }

    private boolean processDescription(String value, Consumer<String> setter) {
        if (value.contains("$descr")) {
            setter.accept(normalize(value.replace("$descr", "").replaceFirst("=", "")));
            return true;
        }
        return false;
    }

    private boolean processType(String value, Consumer<String> setter) {
        if (value.contains("$type")) {
            setter.accept(normalize(value.replace("$type", "").replaceFirst("=", "")));
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
