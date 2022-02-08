package org.jqassistant.contrib.plugin.c4;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jqassistant.contrib.plugin.c4.data.C4Diagram;
import org.jqassistant.contrib.plugin.c4.model.C4Descriptor;
import org.jqassistant.contrib.plugin.c4.model.C4DiagramDescriptor;
import org.jqassistant.contrib.plugin.c4.model.C4FileDescriptor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Scanner plug-in to enrich the graph based on a C4-.puml-file.
 *
 * @author Stephan Pirnbaum
 */
@Requires(FileDescriptor.class)
@Slf4j
public class C4DiagramScannerPlugin extends AbstractScannerPlugin<FileResource, C4Descriptor> {

    @Override
    public boolean accepts(FileResource fileResource, String path, Scope scope) {
        if (!path.toLowerCase().endsWith(".puml")) {
            return false;
        } else {
            try {
                try (InputStream is = fileResource.createStream()) {
                    String fileContent = IOUtils.toString(is);
                    if (fileContent.contains("!include <C4/C4_Component>") ||
                            fileContent.contains("!include <C4/C4_Container>") ||
                            fileContent.contains("!include <C4/C4_Context>")) {
                        return true;
                    }
                }
            } catch (IOException e) {
                log.error("Unable to read C4 diagram", e);
            }
        }
        return false;
    }

    @Override
    public C4Descriptor scan(FileResource fileResource, String path, Scope scope, Scanner scanner) throws IOException {
        C4FileDescriptor c4FileDescriptor = getScannerContext().getStore().addDescriptorType(getScannerContext().getCurrentDescriptor(), C4FileDescriptor.class);
        C4DiagramParser factory = new C4DiagramParser();
        try (InputStream is = fileResource.createStream()) {
            C4Diagram c4Diagram = factory.parseDiagram(is, fileResource.getFile().getName().replace(".puml", ""));
            C4DiagramPersister persister = new C4DiagramPersister(getScannerContext().getStore());
            C4DiagramDescriptor c4DiagramDescriptor = persister.persist(c4Diagram);
            c4FileDescriptor.setDiagram(c4DiagramDescriptor);
        }
        return c4FileDescriptor;
    }
}
