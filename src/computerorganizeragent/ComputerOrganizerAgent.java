/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computerorganizeragent;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class represents a desktop agent that its job is to clean up the desktop
 * by organizing the files into its Agent Directory into appropriate
 * subdirectories. For the next update: _Saved the last modified date of the
 * original files. _Create some threads to handle the moving files.
 *
 * @author ThienDinh
 */
public class ComputerOrganizerAgent {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        File configuration = new File("user.cfg");
        File desktop = null;
        String desktopDirectory = "";
        // If the configuration file is created.
        if (configuration.exists()) {
            Scanner in = new Scanner(configuration);
            if (in.hasNextLine()) {
                desktopDirectory = in.nextLine();
                desktop = new File(desktopDirectory);
            }
        } // Else ask user for the Desktop directory.
        else {
            Scanner in = new Scanner(System.in);
            String userInput = null;
            System.out.print("Please give the Desktop directory path:");
            do {
                userInput = in.nextLine();
                desktop = new File(userInput);
                if (!desktop.exists()) {
                    System.out.print("Cannot locate the path " + desktop.getAbsolutePath()
                            + " to Desktop directory.\nPlease re-enter:");
                    continue;
                } else {
                    System.out.println("All your files in this directory " + desktop.getAbsolutePath() + " will be moved into a new directory named "
                            + desktop.getAbsolutePath() + "\\Agent Directory. Do you want to continue?[Y]es/[N]o");
                    Character c = Character.toUpperCase(in.next().charAt(0));
                    // If the user doesn't want to continue, then exit the program.
                    if (!c.equals('Y')) {
                        System.exit(0);
                    }
                    // Save the Desktop directory.
                    PrintWriter out = new PrintWriter(configuration);
                    out.print(userInput);
                    out.close();
                    // Break the infinite loop.
                    break;
                }
            } while (true);
        }
        // Create an agent object.
        AgentFileProcessor agent = new AgentFileProcessor(desktop);
        // Get files with extensions on Desktop.
        System.out.println("Parent directory:" + desktop.getAbsolutePath());
        System.out.println("Files with extension: ");
        ArrayList<File> extensionFiles = agent.getFilesWithExtensions();
        for (File f : extensionFiles) {
            System.out.println(f.getAbsolutePath());
        }
        // Get all possible extensions on Desktop.
        ArrayList<String> listOfExtensions = agent.getExtensionList();
        System.out.println("All found extensions:" + listOfExtensions);

        // Set up neccessary directories.
        agent.doJob();
        System.out.println("I just organized your desktop files!");
    }

}
