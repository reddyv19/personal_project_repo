// OPLBallot.java
// This file contains the OPLBallot object used in the Election class. Every ballot in an 
// OPL csv file will have it's information stored using this object.
// This file was written by Toby M and Jonathan L
import java.lang.String;

/**
 * Represents an OPL Ballot
 */
public class OPLBallot extends Ballot{
    /**
     * Name of the Party the Ballot is voting for
     */
    private String party;
    
    /**
     * The index of the candidate that the ballot voted for
     */
    private int candidateIndex;

    /**
     * OPL Ballot constructor that sets the ballots ID and the ballots candidates preference
     * @param choice Ballot's csv formatted candidate preferences
     * @param startID Ballot's ID
     */
    OPLBallot(String choice, int startID){
        super(choice, startID);
        String[] preferences = choice.split(",");
        for (int i = 0; i < preferences.length; i++) {
            if(preferences[i].equals("1")){
                candidateIndex = i;
            }
        }
    }

    /**
     * Sets the OPL Ballots preferred party
     * @param party Candidates preferred Party
     */
    public void setParty(String party){
        this.party = party;
    }

    /**
     * Getter for the candidate index
     * @return Candidate index
     */
    public int getCandidateIndex(){
        return candidateIndex;
    }

    /**
     * Getter for the party of the candidate this ballot votes for
     * @return The party associated with the candidate this ballot is voting for
     */
    public String getParty(){
        return party;
    }

    /**
     * Getter for the ID of the ballot
     * @return The ballot ID
     */
    public int getID(){
        return ID;
    }
}
