package webserver;

import java.io.*;

public class HtmlRenderer {
    private String maintenancePageLocation;
    private String rootPageLocation;
    private static HtmlRenderer instance;

    public static HtmlRenderer getInstance() {
        if (instance == null) instance = new HtmlRenderer();

        return instance;
    }

    private HtmlRenderer() { }

    public String renderHtmlPage(String request, OutputStream clientOutputStream) {
        if (request != null) {
            PrintWriter out = new PrintWriter(clientOutputStream, true);
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("\r\n");

            if (WebServer.getInstance().getServerState().equals("Maintenance")) {
                String htmlFilePath = maintenancePageLocation + "/index.html";

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(htmlFilePath));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null)
                        out.println(inputLine);

                    reader.close();
                } catch (Exception e) {
                    out.println("<h1>404 File not found</h1>");
                }

                out.close();

                return "maintenance";
            } else {
                String[] requestParameters = request.split(" ");
                String htmlFilePath = requestParameters[1].equals("/") ? "/index.html" : requestParameters[1];

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(rootPageLocation + htmlFilePath));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null)
                        out.println(inputLine);

                    reader.close();
                } catch (Exception e) {
                    out.println("<h1>404 File not found</h1>");
                    out.close();
                    return "404";
                }

                out.close();

                return htmlFilePath;
            }
        }

        return null;
    }

    public void setRootPageLocation(String rootPageLocation) {
        this.rootPageLocation = rootPageLocation;
    }

    public void setMaintenancePageLocation(String maintenancePageLocation) {
        this.maintenancePageLocation = maintenancePageLocation;
    }
}
