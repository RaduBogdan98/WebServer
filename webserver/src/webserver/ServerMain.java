package webserver;

import java.util.Arrays;

public class ServerMain {
    public static void main(String[] args) {
        Settings settings = Settings.deserializeSettings();

        if (Arrays.asList(args).contains("standalone")) {
            if (settings != null) {
                WebServer.getInstance().setPortNumber(settings.portNumber);

                if (settings.rootDirectory != null && !settings.rootDirectory.isEmpty()) {
                    HtmlRenderer.getInstance().setRootPageLocation(settings.rootDirectory);
                }

                if (settings.maintenanceDirectory != null && !settings.maintenanceDirectory.isEmpty()) {
                    HtmlRenderer.getInstance().setMaintenancePageLocation(settings.maintenanceDirectory);
                }
            }

            WebServer.getInstance().startServer();
        } else {
            new ServerGUI(settings);
        }
    }
}
