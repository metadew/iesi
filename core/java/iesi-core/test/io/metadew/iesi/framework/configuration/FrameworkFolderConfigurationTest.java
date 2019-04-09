package io.metadew.iesi.framework.configuration;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

class FrameworkFolderConfigurationTest {

    @Test
    void folderMapTest() {
        FrameworkFolderConfiguration frameworkFolderConfiguration = new FrameworkFolderConfiguration(
                "resources" + File.separator + "test" + File.separator +"io.metadew.iesi.framework.configuration"
                        + File.separator + "solution_home");
        String[] expectedKeys = new String[]{
                "bin",
                "conf",
                "data",
                "data.mapping",
                "lib",
                "licenses",
                "logs",
                "metadata",
                "metadata.conf",
                "metadata.def",
                "metadata.gen",
                "metadata.in",
                "metadata.in.done",
                "metadata.in.error",
                "metadata.in.new",
                "metadata.in.work",
                "metadata.out",
                "metadata.out.ddl",
                "modules",
                "modules.sqlinsert",
                "modules.sqlinsert.conf",
                "modules.sqlinsert.conf.dbc",
                "modules.sqlinsert.logs",
                "modules.sqlinsert.run",
                "modules.sqlinsert.run.tmp",
                "modules.templates",
                "plugins",
                "run",
                "run.cache",
                "run.exec",
                "run.lock",
                "run.spool",
                "run.tmp",
                "sbin",
                "sys",
                "sys.init"
        };
        assertThat(new ArrayList<>(frameworkFolderConfiguration.getFolderMap().keySet()),
                containsInAnyOrder(expectedKeys));
    }

}
