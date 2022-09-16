package org.jqassistant.contrib.plugin.c4;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import org.jqassistant.contrib.plugin.c4.model.C4DiagramDescriptor;
import org.jqassistant.contrib.plugin.c4.model.C4FileDescriptor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class C4DiagramScannerPluginIT extends AbstractPluginIT {

    @Test
    public void allElementsTest() {
        String presentation = "(presentation:C4:Boundary:Layer:Label1:Label2{alias: 'presentation', name: 'Präsentation'})";
        String web = "(web:C4:Component:Label3:Label4{alias: 'web', name: 'web', var: '2'})";
        String component1 = "(component1:C4:Component:ComponentTag{alias: 'component1', name: 'ComponentName', technologies: ['ComponentTechnology'], description: 'ComponentDescription', Test: '1'})";
        String a ="(a:C4:Component{alias: 'a', name: 'b', technologies: ['c'], description: 'd'})";
        String test111 = "(test111:C4:Person{alias: 'test111', name: 'Test'})";
        String abc = "(abc:C4:Boundary:Example:Layer{alias: 'abc', name: 'a'})";
        String abcSystem = "(abcSystem:C4:System{alias: 'abcSystem', name: 'AbcSystemName', Blub: 'bla'})";
        String def = "(def:C4:Enterprise:Boundary:System{alias: 'def', name: 'd'})";
        String defSystem = "(defSystem:C4:System{alias: 'defSystem', name: 'DefSystemName'})";
        String componentDb1 = "(componentDb1:C4:Component:DB:ComponentDbTag{alias: 'componentDb1', name: 'ComponentDbName', technologies: ['ComponentDbTechnology'], description: 'ComponentDbDescription'})";
        String componentQueue1 = "(componentQueue1:C4:Component:Queue:ComponentQueueTag{alias: 'componentQueue1', name: 'ComponentQueueName', technologies: ['ComponentQueueTechnology'], description: 'ComponentQueueDescription'})";
        String componentExt1 = "(componentExt1:C4:Component:ComponentExtTag{alias: 'component_ext1', name: 'ComponentExtName', technologies: ['ComponentExtTechnology'], description: 'ComponentExtDescription', external: true})";
        String componentDbExt1 = "(componentDbExt1:C4:Component:DB:ComponentDbTag{alias: 'componentDb_ext1', name: 'ComponentDbExtName', technologies: ['ComponentDbExtTechnology'], description: 'ComponentDbExtDescription', external: true})";
        String componentQueueExt1 = "(componentQueueExt1:C4:Component:Queue:ComponentQueueTag{alias: 'componentQueue_ext1', name: 'ComponentQueueExtName', technologies: ['ComponentQueueExtTechnology'], description: 'ComponentQueueExtDescription', external: true})";
        String container1 = "(container1:C4:Container:ContainerTag{alias: 'container1', name: 'ContainerName', technologies: ['ContainerTechnology'], description: 'ContainerDescription'})";
        String containerDb1 = "(containerDb1:C4:Container:DB:ContainerDbTag{alias: 'containerDb1', name: 'ContainerDbName', technologies: ['ContainerDbTechnology'], description: 'ContainerDbDescription'})";
        String containerQueue1 = "(containerQueue1:C4:Container:Queue:ContainerQueueTag{alias: 'containerQueue1', name: 'ContainerQueueName', technologies: ['ContainerQueueTechnology'], description: 'ContainerQueueDescription'})";
        String containerExt1 = "(containerExt1:C4:Container:ContainerExtTag{alias: 'container_ext1', name: 'ContainerExtName', technologies: ['ContainerExtTechnology'], description: 'ContainerExtDescription', external: true})";
        String containerDbExt1 = "(containerDbExt1:C4:Container:DB:ContainerDbTag{alias: 'containerDb_ext1', name: 'ContainerDbExtName', technologies: ['ContainerDbExtTechnology'], description: 'ContainerDbExtDescription', external: true})";
        String containerQueueExt1 = "(containerQueueExt1:C4:Container:Queue:ContainerQueueTag{alias: 'containerQueue_ext1', name: 'ContainerQueueExtName', technologies: ['ContainerQueueExtTechnology'], description: 'ContainerQueueExtDescription', external: true})";
        String system1 = "(system1:C4:System:SystemTag{alias: 'system1', name: 'SystemName', description: 'SystemDescription'})";
        String systemDb1 = "(systemDb1:C4:System:DB:SystemDbTag{alias: 'systemDb1', name: 'SystemDbName', description: 'SystemDbDescription'})";
        String systemQueue1 = "(systemQueue1:C4:System:Queue:SystemQueueTag{alias: 'systemQueue1', name: 'SystemQueueName', description: 'SystemQueueDescription'})";
        String systemExt1 = "(systemExt1:C4:System:SystemExtTag{alias: 'system_ext1', name: 'SystemExtName', description: 'SystemExtDescription', external: true})";
        String systemDbExt1 = "(systemDbExt1:C4:System:DB:SystemDbTag{alias: 'systemDb_ext1', name: 'SystemDbExtName', description: 'SystemDbExtDescription', external: true})";
        String systemQueueExt1 = "(systemQueueExt1:C4:System:Queue:SystemQueueTag{alias: 'systemQueue_ext1', name: 'SystemQueueExtName', description: 'SystemQueueExtDescription', external: true})";
        String label = "(label:C4:Component:abab{alias: 'label', name: 'alias', technologies: ['Tech'], description: 'asjsa'})";

        List<String> elements = Arrays.asList(
                presentation, web, component1, a, test111, abc, abcSystem, def, defSystem,
                componentDb1, componentQueue1, componentExt1, componentDbExt1, componentQueueExt1,
                container1, containerDb1, containerQueue1, containerExt1, containerDbExt1, containerQueueExt1,
                system1, systemDb1, systemQueue1, systemExt1, systemDbExt1, systemQueueExt1,
                label);

        store.beginTransaction();

        C4DiagramDescriptor c4DiagramDescriptor = scanFileAndAssert("AllElements.puml");

        assertThat(c4DiagramDescriptor.getName()).isEqualTo("AllElements");
        assertThat(c4DiagramDescriptor.getBoundaries()).hasSize(3);
        assertThat(c4DiagramDescriptor.getComponents()).hasSize(9);
        assertThat(c4DiagramDescriptor.getContainers()).hasSize(6);
        assertThat(c4DiagramDescriptor.getSystems()).hasSize(9);

        for (String element : elements) {
            TestResult query = query("MATCH " + element + "RETURN count(*) AS cnt");
            long cnt = (long) query.getColumn("cnt").get(0);
            assertThat(cnt).withFailMessage("Unable to identify element: %s", element).isEqualTo(1);
        }

        assertThat(query("MATCH " + presentation + "-[:CONTAINS]->" + web + " RETURN count(*) AS cnt").getColumn("cnt").get(0)).isEqualTo(1L);
        assertThat(query("MATCH " + component1 + "-[:CONTAINS]->" + a + " RETURN count(*) AS cnt").getColumn("cnt").get(0)).isEqualTo(1L);
        assertThat(query("MATCH " + abc + "-[:CONTAINS]->" + abcSystem + " RETURN count(*) AS cnt").getColumn("cnt").get(0)).isEqualTo(1L);
        assertThat(query("MATCH " + def + "-[:CONTAINS]->" + defSystem + " RETURN count(*) AS cnt").getColumn("cnt").get(0)).isEqualTo(1L);
        assertThat(query("MATCH " + abc + "-[:DEPENDS_ON]->" + def + " RETURN count(*) AS cnt").getColumn("cnt").get(0)).isEqualTo(1L);
        assertThat(query("MATCH " + componentDb1 + "-[:DEPENDS_ON]->" + componentQueue1 + " RETURN count(*) AS cnt").getColumn("cnt").get(0)).isEqualTo(1L);
        assertThat(query("MATCH " + componentQueue1 + "-[:DEPENDS_ON]->" + componentDb1 + " RETURN count(*) AS cnt").getColumn("cnt").get(0)).isEqualTo(1L);
        assertThat(query("MATCH " + component1 + "-[:USES_DB{name: 'Uses', technologies: ['Uses_Technology'], A: 'B'}]->" + componentDb1 + " RETURN count(*) AS cnt").getColumn("cnt").get(0)).isEqualTo(1L);

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
