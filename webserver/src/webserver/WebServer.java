package webserver;

import java.net.*;
import java.io.*;

public class WebServer extends Thread {
    protected Socket clientSocket;
    private int portNumber;
    private ServerSocket serverSocket;
    private String serverState;

    private static WebServer instance;

    public static WebServer getInstance() {
        if (instance == null) instance = new WebServer();

        return instance;
    }

    public void stopServer() throws IOException {
        serverState = "Stopped";
        serverSocket.close();
    }

    public void startServerMaintenance() {
        serverState = "Maintenance";
    }

    public void endServerMaintenance() {
        serverState = "Running";
    }

    public void startServer() {
        serverState = "Running";
        serverSocket = null;

        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Connection Socket Created");
            try {
                while (true) {
                    System.out.println("Waiting for Connection");
                    new WebServer(serverSocket.accept()).start();
                }
            } catch (SocketException e) {
                if (serverSocket.isClosed())
                    System.out.println("Connection Closed.");
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10008.");
            System.exit(1);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }

    private WebServer(Socket clientSoc) {
        clientSocket = clientSoc;
    }

    private WebServer() {
        serverState = "Stopped";
    }

    public void run() {
        System.out.println("New Communication Thread Started");

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

            HtmlRenderer.getInstance().renderHtmlPage(in.readLine(), clientSocket.getOutputStream());

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Server: " + inputLine);

                if (inputLine.trim().equals(""))
                    break;
            }

            in.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Problem with Communication Server");
            System.exit(1);
        }
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getServerState() {
        return serverState;
    }
}