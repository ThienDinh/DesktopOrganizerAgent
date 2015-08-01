/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computerorganizeragent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author ThienDinh
 */
public class AgentFileProcessor {

    private final File desktopPath;
    private final File agentDirectory;

    /**
     * Construct an AgentFileCreator.
     *
     * @param desktopPath a File that is a path to Desktop.
     */
    public AgentFileProcessor(File desktopPath) {
        this.desktopPath = desktopPath;
        agentDirectory = new File(desktopPath.getAbsolutePath() + "\\Agent Directory");
    }

    /**
     * Move files to their designated directories. If the file to be moved has
     * the same file name to the other file in the extension directory, then it
     * is not moved.
     *
     * @param extensionFiles
     * @param extDirectories
     * @throws java.io.FileNotFoundException
     */
    private void moveFilesToDirectories(ArrayList<File> extensionFiles,
            ArrayList<File> extDirectories) throws IOException {
        for (File f : extensionFiles) {
            String[] parts = f.getAbsolutePath().split("\\.");
            String ext = parts[parts.length - 1];
            long lastModification = f.lastModified();
            // Assigned it into the appropriate directory.
            for (File d : extDirectories) {
                String dirName = d.getName();
                // If the file's extension matches the extension directory, then move it into this directory.
                if (ext.equals(dirName)) {
                    // Check if there is a file with the same name. If so, not move this file into
                    // extension directory.
                    File[] existingFiles = d.listFiles();
                    boolean isExisting = false;
                    for (int i = 0; i < existingFiles.length; i++) {
                        if (f.getName().equals(existingFiles[i].getName())) {
                            isExisting = !isExisting;
                            break;
                        }
                    }
                    // If the file to be moved is existing in this extension directory, then we're done.
                    if (isExisting) {
                        break;
                    }
                    File newFile = new File(d.getAbsolutePath() + "\\" + f.getName());
                    FileInputStream input = new FileInputStream(f);
                    FileOutputStream output = new FileOutputStream(newFile);
                    int aByte;
                    do {
                        aByte = input.read();
                        if (aByte == -1) {
                            input.close();
                            output.close();
                            newFile.setLastModified(lastModification);
                            // Delete the file on desktop after moving into the folder.
                            f.delete();
                            break;
                        } else {
                            output.write(aByte);
                        }
                    } while (true);
                }
            }
        }
    }

    /**
     * Check whether the agent directory is already created.
     *
     * @param files files gotten from desktop.
     * @return true if existed.
     */
    private boolean isAgentDirectoryCreated() {
        File[] files = desktopPath.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getAbsolutePath().equals(agentDirectory.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ask agent to do its job which is creating its own directory and move
     * files to their locations.
     *
     * @throws IOException
     */
    public void doJob() throws IOException {
        if (!isAgentDirectoryCreated()) {
            agentDirectory.mkdir();
        }
        ArrayList<File> extensionFiles = getFilesWithExtensions();
        ArrayList<String> extensionList = getExtensionList();
        ArrayList<File> extensionDirectories = createExtensionDirectories(extensionList);
        moveFilesToDirectories(extensionFiles, extensionDirectories);
    }

    /**
     * Create extension directories under agent directory using a list of
     * extensions.
     *
     * @param extensionList a list of extensions.
     * @return a list of Files which are paths of the newly created extension
     * directories.
     */
    private ArrayList<File> createExtensionDirectories(ArrayList<String> extensionList) {
        File[] directories = agentDirectory.listFiles();
        ArrayList<File> extDirectories = new ArrayList<>(20);
        // For each extension we've found.
        for (String directoryName : extensionList) {
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
     * Get a list of all extensions from files on Desktop.
     *
     * @return a list of extensions.
     */
    public ArrayList<String> getExtensionList() {
        return getExtensionList(getFilesWithExtensions());
    }

    /**
     * Get all extensions from files. The extension of a file is the last part
     * after the dot in a file name.
     *
     * @param files any list of files.
     * @return list of extensions.
     */
    private ArrayList<String> getExtensionList(ArrayList<File> files) {
        // extensions list stores all found extensions.
        ArrayList<String> extensions = new ArrayList<>(40);
        int numberOfFiles = files.size();
        for (int i = 0; i < numberOfFiles; i++) {
            String[] parts = files.get(i).getName().split("\\.");
            if (parts.length <= 1) {
                continue;
            }
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
     * Get files that have extension on Desktop.
     *
     * @return a list of files with extension.
     */
    public ArrayList<File> getFilesWithExtensions() {
        return getFilesWithExtensions(desktopPath.listFiles());
    }

    /**
     * Get files that have extensions.
     *
     * @param files any list of files.
     * @return a list of files with extension.
     */
    private ArrayList<File> getFilesWithExtensions(File[] files) {
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

}
