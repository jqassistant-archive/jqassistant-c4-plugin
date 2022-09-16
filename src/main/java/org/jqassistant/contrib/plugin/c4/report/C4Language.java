package org.jqassistant.contrib.plugin.c4.report;

import com.buschmais.jqassistant.core.report.api.SourceProvider;
import com.buschmais.jqassistant.core.report.api.model.Language;
import com.buschmais.jqassistant.core.report.api.model.LanguageElement;
import org.jqassistant.contrib.plugin.c4.model.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the language elements for "C4"
 */
@Language
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface C4Language {

    C4LanguageElement value();

    enum C4LanguageElement implements LanguageElement {

        Boundary {
            @Override
            public SourceProvider<BoundaryDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        },

        Component {
            @Override
            public SourceProvider<ComponentDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        },

        Container {
            @Override
            public SourceProvider<ContainerDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        },

        Person {
            @Override
            public SourceProvider<PersonDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        },

        System {
            @Override
            public SourceProvider<SystemDescriptor> getSourceProvider() {
                return ElementDescriptor::getName;
            }
        };

        @Override
        public String getLanguage() {
            return "C4";
        }
    }
}
