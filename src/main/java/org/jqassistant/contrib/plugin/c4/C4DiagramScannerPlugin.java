package org.jqassistant.contrib.plugin.c4;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import org.jqassistant.contrib.plugin.c4.data.C4Diagram;
import org.jqassistant.contrib.plugin.c4.model.C4Descriptor;
import org.jqassistant.contrib.plugin.c4.model.C4DiagramDescriptor;
import org.jqassistant.contrib.plugin.c4.model.C4FileDescriptor;

import java.io.IOException;

/**
 * Scanner plug-in to enrich the graph based on a C4-.puml-file.
 *
 * @author Stephan Pirnbaum
 */
@Requires(FileDescriptor.class)
public class C4DiagramScannerPlugin extends AbstractScannerPlugin<FileResource, C4Descriptor> {

    @Override
    public boolean accepts(FileResource fileResource, String path, Scope scope) {
        return path.toLowerCase().endsWith(".puml");
    }

    @Override
    public C4Descriptor scan(FileResource fileResource, String path, Scope scope, Scanner scanner) throws IOException {
        C4FileDescriptor c4FileDescriptor = getScannerContext().getStore().addDescriptorType(getScannerContext().getCurrentDescriptor(), C4FileDescriptor.class);
        C4DiagramParser factory = new C4DiagramParser();
        C4Diagram c4Diagram = factory.parseDiagram(fileResource.createStream());
        C4DiagramPersister persister = new C4DiagramPersister(getScannerContext().getStore());
        C4DiagramDescriptor c4DiagramDescriptor = persister.persist(c4Diagram);
        c4FileDescriptor.setDiagram(c4DiagramDescriptor);
        return c4FileDescriptor;
    }
}
