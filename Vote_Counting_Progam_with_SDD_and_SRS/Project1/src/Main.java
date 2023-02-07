// Main.java
// The purpose of this file is to gather user input on the name of the file and instantiate 
// the Election object used to deteermine the winner of an election.
// This file was written by Toby M and Jonathan L
import java.io.File;
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
        int count = 0;
        Scanner s = new Scanner(System.in);
        File f = null;
        while (count < 5){
            System.out.println("Please input the ballot file name ('filename.csv'):");
            String filename = s.nextLine();
            f = openFile(filename);
            if (f!= null){
                count = 0;
                break;
            }

            System.err.println("File does not exist");

            count++;

        }
        if (count == 5){
            System.out.println("File could not be opened or does not exist.\nPlease ensure the ballot file is in the directory and try again.");
            return;
        }

        Election election = new Election(f);
        long startTime = System.nanoTime();
        election.runElection();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        election.displayWinners(duration);

    }


}