/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computerorganizeragent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents a desktop agent that its job is to clean up the desktop by organizing the files
 * into its Agent Directory into appropriate subdirectories.
 * For the next update:
 * _Saved the last modified date of the original files.
 * _Create some threads to handle the moving files.
 * @author ThienDinh
 */
public class ComputerOrganizerAgent {

    private static File agentDirectory;

    /**
     * Get all extensions from files.
     *
     * @param files files.
     * @return list of extensions.
     */
    private static ArrayList<String> getExtensionSet(ArrayList<File> files) {
        // extensions list stores all found extensions.
        ArrayList<String> extensions = new ArrayList<>(40);
        int numberOfFiles = files.size();
        for (int i = 0; i < numberOfFiles; i++) {
            String[] parts = files.get(i).getName().split("\\.");
            // Some files have name contains the dot character.
            String extension = parts[parts.length - 1];
            boolean doesContain = false;
            for (int j = 0; j < extensions.size(); j++) {
                if (extensions.get(j).equals(extension)) {
                    doesContain = true;
                }
            }
            // If the extensions set does not contain this extension,
            // then add it.
            if (!doesContain) {
                extensions.add(extension);
            }
        }
        return extensions;
    }

    /**
     * Check whether the agent directory is already created.
     *
     * @param files files gotten from desktop.
     * @return true if existed.
     */
    private static boolean isAgentDirectoryCreated(File[] files) {
        for (int i = 0; i < files.length; i++) {
            if (files[i].getAbsolutePath().equals(ComputerOrganizerAgent.agentDirectory.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get files that are actually files, and they also have extensions.
     *
     * @param files desktop files.
     * @return interested files.
     */
    private static ArrayList<File> getInterestedFiles(File[] files) {
        ArrayList<File> interestedFiles = new ArrayList<>(50);
        int numberOfFiles = files.length;
        for (int i = 0; i < numberOfFiles; i++) {
            // Not accept directory.
            if (!files[i].isFile()) {
                continue;
            }
            String[] parts = files[i].getName().split("\\.");
            // Not accept file without extension.
            if (parts.length <= 1) {
                continue;
            }
            interestedFiles.add(files[i]);
        }
        return interestedFiles;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        File desktop = new File("C:\\Users\\ThienDinh\\Desktop");
        agentDirectory = new File("C:\\Users\\ThienDinh\\Desktop\\Agent Directory");
        System.out.println("Parent directory:" + desktop.getAbsolutePath());
        File[] desktopFiles = desktop.listFiles();
        System.out.println("Child files: ");
        for (File f : desktopFiles) {
            System.out.println(f.getName());
        }
        System.out.print("Extensions: ");
        ArrayList<File> interestedFiles = getInterestedFiles(desktopFiles);
        ArrayList<String> extensions = getExtensionSet(interestedFiles);
        System.out.println(extensions);
        // Create agentDirectory if it's not been created.
        if (!isAgentDirectoryCreated(desktopFiles)) {
            agentDirectory.mkdir();
        }
        // Create extensions directories.
        ArrayList<File> extDirectories = createExtensionDirectories(extensions);
        // Move files to their designated directories.
        moveFilesToDirectories(interestedFiles, extDirectories);
    }

    /**
     * Create extension directories.
     *
     * @param extensionSet
     */
    private static ArrayList<File> createExtensionDirectories(ArrayList<String> extensionSet) {
        File[] directories = agentDirectory.listFiles();
        ArrayList<File> extDirectories = new ArrayList<>(20);
        // For each extension we've found.
        for (String directoryName : extensionSet) {
            File subDirectory = new File(agentDirectory.getAbsolutePath() + "\\" + directoryName);
            extDirectories.add(subDirectory);
            // Check if this subDirectory is existing.
            boolean isExisting = false;
            for (int j = 0; j < directories.length; j++) {
                // If there is a directory with the same path, then the directory does exist.
                if (subDirectory.getAbsolutePath().equals(directories[j].getAbsolutePath())) {
                    isExisting = true;
                    break;
                }
            }
            if (!isExisting) {
                subDirectory.mkdir();
            }
        }
        return extDirectories;
    }

    /**
     * Move files to their designated directories.
     *
     * @param interestedFiles files.
     */
    private static void moveFilesToDirectories(ArrayList<File> interestedFiles,
            ArrayList<File> extDirectories) {
        for (File f : interestedFiles) {
            String[] parts = f.getAbsolutePath().split("\\.");
            String ext = parts[parts.length - 1];
            // Assigned it into the appropriate directory.
            for (File d : extDirectories) {
                String dirName = d.getName();
                // If the file's extension matches the extension directory, then move it into this directory.
                if (ext.equals(dirName)) {
                    File newFile = new File(d.getAbsolutePath() + "\\" + f.getName());
                    try {
                        FileInputStream input = new FileInputStream(f);
                        FileOutputStream output = new FileOutputStream(newFile);
                        int aByte;
                        do {
                            aByte = input.read();
                            if (aByte == -1) {
                                input.close();
                                output.close();
                                break;
                            } else {
                                output.write(aByte);
                            }
                        } while (true);
                    } catch (FileNotFoundException ex) {
                        System.out.println("File " + f.getAbsolutePath() + " does not exist or was not found.");
                    } catch (IOException ex) {
                        System.out.println("Cannot move file " + f.getAbsolutePath() + " into Agent Directory.\n" + ex);
                    }
                }
            }
        }
    }

}
