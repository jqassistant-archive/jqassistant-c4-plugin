package org.jqassistant.contrib.plugin.c4;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jqassistant.contrib.plugin.c4.data.*;
import org.jqassistant.contrib.plugin.c4.antlr.C4BaseListener;
import org.jqassistant.contrib.plugin.c4.antlr.C4Parser;
import org.jqassistant.contrib.plugin.c4.data.*;

import java.util.*;

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
    private final List<C4Element> c4Elements;
    @Getter
    private final List<C4ElementRelation> c4ElementRelations;

    private final ArrayList<C4Element> parentHierarchy = new ArrayList<>();
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
        properties.put(normalize(ctx.key.getText()), normalize(ctx.value.getText()));
    }

    @Override
    public void exitComponent(C4Parser.ComponentContext ctx) {
        C4Component.C4ComponentBuilder elementBuilder = C4Component.builder()
                .alias(normalize(ctx.paramList().alias.getText()))
                .name(normalize(ctx.paramList().label.getText()))
                .secondaryElementType(ctx.secondaryType)
                .external(ctx.external)
                .properties(properties);

        if (parentHierarchy.size() > 0) {
            elementBuilder.parent(parentHierarchy.get(parentHierarchy.size() - 1));
        }
        C4Element element = processParameters(elementBuilder, ctx.paramList());

        c4Elements.add(element);
        properties = new HashMap<>();
    }

    @Override
    public void exitContainer(C4Parser.ContainerContext ctx) {
        C4Container.C4ContainerBuilder elementBuilder = C4Container.builder()
                .alias(normalize(ctx.paramList().alias.getText()))
                .name(normalize(ctx.paramList().label.getText()))
                .secondaryElementType(ctx.secondaryType)
                .external(ctx.external)
                .properties(properties);

        if (parentHierarchy.size() > 0) {
            elementBuilder.parent(parentHierarchy.get(parentHierarchy.size() - 1));
        }

        C4Element element = processParameters(elementBuilder, ctx.paramList());

        c4Elements.add(element);
        properties = new HashMap<>();
    }

    @Override
    public void exitSystem(C4Parser.SystemContext ctx) {
        C4System.C4SystemBuilder elementBuilder = C4System.builder()
                .alias(normalize(ctx.systemParamList().alias.getText()))
                .name(normalize(ctx.systemParamList().label.getText()))
                .secondaryElementType(ctx.secondaryType)
                .external(ctx.external)
                .properties(properties);

        if (parentHierarchy.size() > 0) {
            elementBuilder.parent(parentHierarchy.get(parentHierarchy.size() - 1));
        }

        C4Element element = processParameters(elementBuilder, ctx.systemParamList());

        c4Elements.add(element);
        properties = new HashMap<>();
    }

    private C4Element processParameters(C4Element.C4ElementBuilder builder, C4Parser.ParamListContext paramListContext) {
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

    private C4Element processParameters(C4Element.C4ElementBuilder builder, C4Parser.SystemParamListContext paramListContext) {
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
        C4ElementRelation.C4ElementRelationBuilder relationBuilder = C4ElementRelation.builder()
                .from(normalize(ctx.relParamList().from.getText()))
                .to(normalize(ctx.relParamList().to.getText()))
                .name(normalize(ctx.relParamList().label.getText()))
                .properties(properties);

        C4ElementRelation relation = processParameters(relationBuilder, ctx.relParamList());

        c4ElementRelations.add(relation);
        properties = new HashMap<>();
    }

    private C4ElementRelation processParameters(C4ElementRelation.C4ElementRelationBuilder builder, C4Parser.RelParamListContext paramListContext) {
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

    private boolean setSpecifiedProperty(String value, C4Element.C4ElementBuilder builder) {
        if (value.contains("$tags")) {
            builder.stereotypes(stream(value.replace("$tags", "").replaceFirst("=", "").split(",")).map(this::normalize).collect(toSet()));
            return true;
        } else if (value.contains("$descr")) {
            builder.description(normalize(value.replace("$descr", "").replaceFirst("=", "")));
            return true;
        } else {
            return value.contains("$sprite") || value.contains("$link"); // will be ignored
        }
    }

    private boolean setSpecifiedProperty(String value, C4ElementRelation.C4ElementRelationBuilder builder) {
        if (value.contains("$tags")) {
            builder.stereotypes(stream(value.replace("$tags", "").replaceFirst("=", "").split(",")).map(this::normalize).collect(toSet()));
            return true;
        } else if (value.contains("$descr")) {
            builder.description(normalize(value.replace("$descr", "").replaceFirst("=", "")));
            return true;
        } else {
            return value.contains("$sprite") || value.contains("$link"); // will be ignored
        }
    }

    private String normalize(String text) {
        if (text == null) {
            return text;
        } else {
            return text.replace("\"", "").trim();
        }
    }
}
