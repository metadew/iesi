package io.metadew.iesi.gcp.services.bqloader.launch;

import io.metadew.iesi.gcp.services.bqloader.common.configuration.Settings;
import io.metadew.iesi.gcp.common.framework.FrameworkService;
import picocli.CommandLine.Command;

@Command(
        name = "create"
)
public class BqlInstanceCreateCommand implements Runnable {
    @Override
    public void run() {
        FrameworkService.getInstance().clean(Settings.CODE.value());
        FrameworkService.getInstance().create(Settings.CODE.value());
    }
}
