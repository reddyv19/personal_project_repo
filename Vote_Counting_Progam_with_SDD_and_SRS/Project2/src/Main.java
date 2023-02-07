// Main.java
// The purpose of this file is to gather user input on the name of the file and instantiate 
// the Election object used to determine the winner of an election.
// This file was written by Toby M and Jonathan L
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class to run an election
 */
public class Main {
    /**
     * Election Object that will be used to process the incoming ballots.
     */
    public Election election;

    /**
     * This will ensure that the file exists and opens it
     * @param name Name of the ballot file within the directory
     * @return Opened File
     */
    public static File openFile(String name){ //public for testing purposes
        if(name.length()<4){
            System.err.println("Filename is too short");
            return null;
        }
        String extension = name.substring(name.length() - 3);
        if (extension.equals(".csv")){
            System.err.println("Incorrect file type. Ensure file is CSV formatted.");
            return null;
        }
        File f = new File(name);
        if (f.exists()){
            return f;
        }
        return null;
    }

    /**
     * Main function that will run the entire election processing algorithm
     * @param args Commandline arguments
     */
    public static void main(String[] args) {
        //attempts at inputing a valid file name
        int count = 0;
        Scanner s = new Scanner(System.in);
        ArrayList<File> files = new ArrayList<>();
        File f = null;
        boolean fileFlag = false;

        //while the user wants to add more files
        do {

            while (count < 5) {
                System.out.println("Please input the ballot file name ('filename.csv'):");
                String filename = s.nextLine();
                File newFile = openFile(filename);
                if (newFile == null) {
                    System.err.println("Invalid file name");
                } else {
                    files.add(newFile);
                    if (files.get(files.size() - 1) != null) {
                        count = 0;
                        break;
                    }
                }


                System.err.println("File does not exist");

                count++;

            }
            if (count == 5) {
                System.out.println("File could not be opened or does not exist.\nPlease ensure the ballot file is in the directory and try again.");
                return;
            }

            System.out.println("Do you want another file? [y/n]");
            String answer = s.nextLine();
            if (!answer.equals("y")) {
                fileFlag = true;
            }

        } while (!fileFlag);


        Election election = new Election(files);
        long startTime = System.nanoTime();
        election.runElection();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        election.displayWinners(duration);

    }


}