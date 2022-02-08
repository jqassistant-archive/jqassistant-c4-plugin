package org.jqassistant.contrib.plugin.c4;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.jqassistant.contrib.plugin.c4.model.C4DiagramDescriptor;
import org.jqassistant.contrib.plugin.c4.model.C4FileDescriptor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class C4DiagramScannerPluginIT extends AbstractPluginIT {

    @Test
    public void testInsuranceExample1() throws IOException {
        store.beginTransaction();

        C4DiagramDescriptor c4DiagramDescriptor = scanFileAndAssert("AllElements.puml");
        assertThat(c4DiagramDescriptor.getName()).isEqualTo("AllElements");
        assertThat(c4DiagramDescriptor.getBoundaries()).hasSize(3);
        assertThat(c4DiagramDescriptor.getComponents()).hasSize(9);
        assertThat(c4DiagramDescriptor.getContainers()).hasSize(6);
        assertThat(c4DiagramDescriptor.getSystems()).hasSize(9);

        store.commitTransaction();
    }

    private C4DiagramDescriptor scanFileAndAssert(String fileName) {
        File testFile = new File(getClassesDirectory(C4DiagramScannerPluginIT.class), fileName);
        Descriptor descriptor = getScanner().scan(testFile, fileName, DefaultScope.NONE);
        assertThat(descriptor).isInstanceOf(C4FileDescriptor.class);
        C4FileDescriptor c4FileDescriptor = (C4FileDescriptor) descriptor;
        assertThat(c4FileDescriptor.getDiagram()).isNotNull();

        return c4FileDescriptor.getDiagram();
    }

}
