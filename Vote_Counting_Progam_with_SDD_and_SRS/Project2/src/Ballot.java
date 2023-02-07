// Ballot.java
// This file contains the Parent class for both the IR and OPL Ballot classes. It implements 
// the methods that relate to the ID of a ballot since that is shared among both children classes.
// This file was written by Toby M and Jonathan L
/**
 * Represents a Ballot in an election
 */
public abstract class Ballot {

    /**
     * Ballot's unique ID
     */
    protected int ID;

    /**
     * Get Ballot's ID
     *
     * @return Ballot's ID
     */
    public int getID(){
        return ID;
    }

    /**
     * Constructor for the abstract Ballot Class
     * @param choice Ballot's csv formatted candidate preferences
     */
    Ballot(String choice, int startID){
        ID = startID;
    }
}
