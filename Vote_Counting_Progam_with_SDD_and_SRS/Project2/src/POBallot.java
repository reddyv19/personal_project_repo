// POBallot.java
// This file contains the PO object used in the Election class. Every ballot in an
// PO csv file will have it's information stored using this object.
// This file was written by Jonathan L

import java.lang.String;
/**
 * Represents a PO Ballot
 */
public class POBallot extends Ballot {

    /**
     * The index of the candidate that the ballot voted for
     */
    private int candidateIndex;

    /**
     * PO Ballot constructor that sets the ballots ID and the ballots candidates preference
     * @param choice Ballot's csv formatted candidate preferences
     * @param startID Ballot's ID
     */
    POBallot(String choice, int startID){
        super(choice, startID);
        String[] preferences = choice.split(",");
        for (int i = 0; i < preferences.length; i++) {
            if(preferences[i].equals("1")){
                candidateIndex = i;
            }
        }
    }

    /**
     * Getter for the candidate index
     * @return Candidate index
     */
    public int getCandidateIndex(){
        return candidateIndex;
    }

    /**
     * Getter for the ID of the ballot
     * @return The ballot ID
     */
    public int getID(){
        return ID;
    }
}
