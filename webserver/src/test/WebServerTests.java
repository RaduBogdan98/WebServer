package test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import webserver.HtmlRenderer;
import webserver.WebServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebServerTests {
    @BeforeClass
    public static void setUp() {
        WebServer.getInstance().setPortNumber(8080);
        HtmlRenderer.getInstance().setRootPageLocation("./src/html_resources");
        HtmlRenderer.getInstance().setMaintenancePageLocation("./src/maintenance");
    }

    @Test
    public void correctServerStopStatusChange() throws IOException {
        if (WebServer.getInstance().getServerState().equals("Running")) {
            WebServer.getInstance().stopServer();
        }

        Assert.assertEquals("Stopped", WebServer.getInstance().getServerState());
    }

    @Test
    public void correctServerStartStatusChange() throws IOException {
        if (WebServer.getInstance().getServerState().equals("Stopped")) {
            new Thread(
                    () -> WebServer.getInstance().startServer()
            ).start();
        }
        else if(WebServer.getInstance().getServerState().equals("Maintenance")){
            WebServer.getInstance().endServerMaintenance();
        }

        while (WebServer.getInstance().getServerState() != "Running" && WebServer.getInstance().getServerState() != "Maintenance"); //Wait for the server to start
        Assert.assertEquals("Running", WebServer.getInstance().getServerState());
    }

    @Test
    public void correctServerMaintenanceStatusChange() throws IOException {
        if (WebServer.getInstance().getServerState().equals("Stopped")) {
            new Thread(
                    () -> WebServer.getInstance().startServer()
            ).start();
        }

        while (WebServer.getInstance().getServerState() != "Running" && WebServer.getInstance().getServerState() != "Maintenance"); //Wait for the server to start
        WebServer.getInstance().startServerMaintenance(); //Set server State to Maintenance

        Assert.assertEquals("Maintenance", WebServer.getInstance().getServerState());
    }

    @Test(expected = ConnectException.class)
    public void connectionFailOnStoppedServerTest() throws IOException {
        if (WebServer.getInstance().getServerState().equals("Running")) {
            WebServer.getInstance().stopServer();
        }

        URL url = new URL("http://localhost:8080");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoInput(true);
        httpConn.setDoOutput(false); // false indicates this is a GET request
        httpConn.getResponseCode(); // this call throws the expected exception if connection is refused
    }

    @Test
    public void connectionWorkingOnStoppedRunningTest() {
        try {
            if (WebServer.getInstance().getServerState().equals("Stopped")) {
                new Thread(
                        () -> WebServer.getInstance().startServer()
                ).start();
            }

            URL url = new URL("http://localhost:8080");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoInput(true);
            httpConn.setDoOutput(false); // false indicates this is a GET request
            Assert.assertTrue(httpConn.getResponseCode() > 0);

        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void maintenanceResponeForRequestsTest() {
        try {
            if (WebServer.getInstance().getServerState().equals("Stopped")) {
                new Thread(
                        () -> WebServer.getInstance().startServer()
                ).start();
            }

            while (WebServer.getInstance().getServerState() != "Running"); //Wait for the server to start
            WebServer.getInstance().startServerMaintenance(); //Set server State to Maintenance

            URL url = new URL("http://localhost:8080");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoInput(true);
            httpConn.setDoOutput(false); // false indicates this is a GET request
            Assert.assertTrue(httpConn.getResponseCode() > 0); //Test if connection to the server is possible

            String filePath = HtmlRenderer.getInstance().renderHtmlPage("GET /random.html", new ByteArrayOutputStream());
            Assert.assertTrue(filePath.equals("maintenance")); //Test if no matter the request, the maintenance page is displayed
        } catch (IOException e) {
            Assert.fail();
        }
    }
}
