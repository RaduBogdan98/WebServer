package webserver;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerGUI {
    //region Fields
    private Settings settings;

    //region GUI Member Fields
    private JButton statusChangeButton;
    private JCheckBox maintenanceModeCheck;
    private JLabel serverStatusLabel;
    private JLabel serverAddressLabel;
    private JLabel serverPortLabel;
    private JTextField serverPort;
    private JTextField rootDirectoryAddress;
    private JTextField maintenanceDirectoryAddress;
    private JPanel rootDirectoryRectangle;
    private JPanel maintenanceDirectoryRectangle;
    private JButton browseRootDirectory;
    private JButton browseMaintenanceDirectory;
    //endregion

    //endregion

    public ServerGUI(Settings settings) {
        this.settings = settings;
        createGUI();
    }

    private void createGUI() {
        JFrame frame = new JFrame();

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel topSection = new JPanel(new FlowLayout());

        topSection.add(this.createInfoArea());
        topSection.add(this.createControlArea());

        mainPanel.add(topSection);
        mainPanel.add(this.createConfigArea());

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (settings != null) {
                    settings.maintenanceDirectory = maintenanceDirectoryAddress.getText();
                    settings.rootDirectory = rootDirectoryAddress.getText();
                    settings.portNumber = Integer.parseInt(serverPort.getText());
                } else {
                    settings = new Settings(rootDirectoryAddress.getText(), maintenanceDirectoryAddress.getText(), Integer.parseInt(serverPort.getText()));
                }

                settings.serializeSettings();
            }
        });
        frame.setTitle("VVS Web Server");
        frame.pack();
        frame.setSize(new Dimension(600, 400));
        frame.setVisible(true);
    }

    //region Main Panel Areas
    private JPanel createControlArea() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createTitledBorder("Server Control"));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        statusChangeButton = new JButton("Start Server");
        statusChangeButton.addActionListener(
                (event) ->
                {
                    if (statusChangeButton.getText().equals("Start Server")) {
                        startServer();
                    } else {
                        stopServer();
                    }
                });

        maintenanceModeCheck = new

                JCheckBox("Switch To Maintenance");
        maintenanceModeCheck.setEnabled(false);
        maintenanceModeCheck.addActionListener((event) ->

                onMaintenanceCheckStatusChange());

        controlPanel.add(statusChangeButton);
        controlPanel.add(maintenanceModeCheck);

        return controlPanel;
    }

    private JPanel createInfoArea() {
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Server Info"));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JPanel panel1 = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Server status: ");
        serverStatusLabel = new JLabel("not running");
        panel1.add(label);
        panel1.add(serverStatusLabel);

        JPanel panel2 = new JPanel(new FlowLayout());
        label = new JLabel("Server address: ");
        serverAddressLabel = new JLabel("not running");
        panel2.add(label);
        panel2.add(serverAddressLabel);

        JPanel panel3 = new JPanel(new FlowLayout());
        label = new JLabel("Server listening port: ");
        serverPortLabel = new JLabel("not running");
        panel3.add(label);
        panel3.add(serverPortLabel);

        infoPanel.add(panel1);
        infoPanel.add(panel2);
        infoPanel.add(panel3);

        return infoPanel;
    }

    private JPanel createConfigArea() {
        JPanel configPanel = new JPanel();
        configPanel.setBorder(BorderFactory.createTitledBorder("Server Configuration"));
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));

        JPanel panel1 = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Server listening port: ");
        serverPort = new JTextField(6);
        if (settings != null) {
            serverPort.setText(settings.portNumber + "");
        }
        panel1.add(label);
        panel1.add(serverPort);

        JPanel panel2 = new JPanel(new FlowLayout());
        label = new JLabel("Root directory: ");
        rootDirectoryAddress = new JTextField(20);
        if (settings != null) {
            rootDirectoryAddress.setText(settings.rootDirectory);
        }
        rootDirectoryAddress.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            private void onChange() {
                if (rootDirectoryRectangle != null) {
                    if (rootDirectoryAddress.getText() != null && new File(rootDirectoryAddress.getText()).isDirectory()) {
                        rootDirectoryRectangle.setBackground(Color.GREEN);
                    } else {
                        rootDirectoryRectangle.setBackground(Color.RED);
                    }
                }
            }
        });

        browseRootDirectory = new JButton("...");
        browseRootDirectory.addActionListener((event) -> selectLocation(rootDirectoryAddress));

        rootDirectoryRectangle = new JPanel();
        if (rootDirectoryAddress.getText() != null && new File(rootDirectoryAddress.getText()).isDirectory()) {
            rootDirectoryRectangle.setBackground(Color.GREEN);
        } else {
            rootDirectoryRectangle.setBackground(Color.RED);
        }
        rootDirectoryRectangle.setSize(new Dimension(10, 10));

        panel2.add(label);
        panel2.add(rootDirectoryAddress);
        panel2.add(browseRootDirectory);
        panel2.add(rootDirectoryRectangle);

        JPanel panel3 = new JPanel(new FlowLayout());
        label = new JLabel("Maintenance directory: ");
        maintenanceDirectoryAddress = new JTextField(20);
        if (settings != null) {
            maintenanceDirectoryAddress.setText(settings.maintenanceDirectory);
        }
        maintenanceDirectoryAddress.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            private void onChange() {
                if (maintenanceDirectoryRectangle != null) {
                    if (maintenanceDirectoryAddress.getText() != null && new File(maintenanceDirectoryAddress.getText()).isDirectory()) {
                        maintenanceDirectoryRectangle.setBackground(Color.GREEN);
                    } else {
                        maintenanceDirectoryRectangle.setBackground(Color.RED);
                    }
                }
            }
        });

        browseMaintenanceDirectory = new JButton("...");
        browseMaintenanceDirectory.addActionListener((event) -> selectLocation(maintenanceDirectoryAddress));

        maintenanceDirectoryRectangle = new JPanel();
        if (maintenanceDirectoryAddress.getText() != null && new File(maintenanceDirectoryAddress.getText()).isDirectory()) {
            maintenanceDirectoryRectangle.setBackground(Color.GREEN);
        } else {
            maintenanceDirectoryRectangle.setBackground(Color.RED);
        }
        maintenanceDirectoryRectangle.setSize(new Dimension(10, 10));

        panel3.add(label);
        panel3.add(maintenanceDirectoryAddress);
        panel3.add(browseMaintenanceDirectory);
        panel3.add(maintenanceDirectoryRectangle);

        configPanel.add(panel1);
        configPanel.add(panel2);
        configPanel.add(panel3);

        return configPanel;
    }
    //endregion

    //region Action Listeners
    private void selectLocation(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startServer(){
        if (!serverPort.getText().isEmpty() && !rootDirectoryAddress.getText().isEmpty()) {
            WebServer.getInstance().setPortNumber(Integer.parseInt(serverPort.getText()));
            HtmlRenderer.getInstance().setRootPageLocation(rootDirectoryAddress.getText());

            new Thread(() -> WebServer.getInstance().startServer()).start();

            maintenanceModeCheck.setEnabled(true);
            statusChangeButton.setText("Stop Server");
            serverStatusLabel.setText("running");
            serverPortLabel.setText(serverPort.getText());

            try{
                serverAddressLabel.setText(InetAddress.getLocalHost().getHostAddress());
            }
            catch (UnknownHostException e){
                e.printStackTrace();
            }

            serverPort.setEnabled(false);
            rootDirectoryAddress.setEnabled(false);
            browseRootDirectory.setEnabled(false);
        }
    }

    private void stopServer() {
        new Thread(() -> {
            try {
                WebServer.getInstance().stopServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        maintenanceModeCheck.setEnabled(false);
        maintenanceModeCheck.setSelected(false);
        statusChangeButton.setText("Start Server");
        serverStatusLabel.setText("not running");
        serverPortLabel.setText("not running");
        serverAddressLabel.setText("not running");

        serverPort.setEnabled(true);
        rootDirectoryAddress.setEnabled(true);
        maintenanceDirectoryAddress.setEnabled(true);
        browseRootDirectory.setEnabled(true);
        browseMaintenanceDirectory.setEnabled(true);
    }

    private void onMaintenanceCheckStatusChange() {
        if (maintenanceModeCheck.isSelected()) {
            WebServer.getInstance().startServerMaintenance();
            HtmlRenderer.getInstance().setMaintenancePageLocation(maintenanceDirectoryAddress.getText());
            serverStatusLabel.setText("maintenance");
            maintenanceDirectoryAddress.setEnabled(false);
            browseMaintenanceDirectory.setEnabled(false);
            rootDirectoryAddress.setEnabled(true);
            browseRootDirectory.setEnabled(true);
        } else {
            WebServer.getInstance().endServerMaintenance();
            HtmlRenderer.getInstance().setRootPageLocation(rootDirectoryAddress.getText());
            serverStatusLabel.setText("running");
            maintenanceDirectoryAddress.setEnabled(true);
            browseMaintenanceDirectory.setEnabled(true);
            rootDirectoryAddress.setEnabled(false);
            browseRootDirectory.setEnabled(false);
        }
    }
    //endregion
}
