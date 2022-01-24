package org.jqassistant.contrib.plugin.c4diagram;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import lombok.Builder;
import lombok.Singular;
import org.apache.commons.io.FileUtils;
import org.jqassistant.contrib.plugin.c4diagram.data.C4Diagram;
import org.jqassistant.contrib.plugin.c4diagram.model.C4DiagramDescriptor;
import org.jqassistant.contrib.plugin.c4diagram.model.C4FileDescriptor;
import org.junit.jupiter.api.Test;

import java.awt.image.ComponentColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class C4DiagramScannerPluginIT extends AbstractPluginIT {


    @Test
    public void testInsuranceExample1() throws IOException {
        store.beginTransaction();

        C4DiagramDescriptor c4DiagramDescriptor = scanFileAndAssert("ComponentDiagram.puml");

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
