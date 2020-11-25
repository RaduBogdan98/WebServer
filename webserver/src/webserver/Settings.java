package webserver;

import java.io.*;

public class Settings implements Serializable {
    protected String rootDirectory;
    protected String maintenanceDirectory;
    protected int portNumber;
    private static final String filename = "settings.ser";

    public Settings(String rootDirectory, String maintenanceDirectory, int portNumber) {
        this.rootDirectory = rootDirectory;
        this.maintenanceDirectory = maintenanceDirectory;
        this.portNumber = portNumber;
    }

    public void serializeSettings() {
        // Serialization
        try {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(this);

            out.close();
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Settings deserializeSettings() {
        // Deserialization
        try {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            Settings settings = (Settings) in.readObject();
            in.close();
            file.close();

            return settings;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
