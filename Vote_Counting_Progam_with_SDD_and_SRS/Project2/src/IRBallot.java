// IRBallot.java
// This file contains the IRBallot object used in the Election class. Every ballot in an 
// IR csv file will have it's information stored using this object.
// This file was written by Toby M and Jonathan L
import java.lang.String;

/**
 * Represents an IR Ballot
 */
public class IRBallot extends Ballot{

    /**
     * Array with a ballots rankings of all the candidates
     */
    private int[] candidateRankIndex;

    /**
     * What choice the ballot is on
     * (i.e. what number are we looking for in the ballot)
     */
    private int currentRank;

    /**
     * Index where the best current candidate is in the candidate array
     */
    private int currentRankIndex;

    /**
     * The number of candidates that actually received a rank
     */
    private int numCandidatesRanked;


    /**
     * IR Ballot constructor that sets the ballots ID and the ballots candidates preference
     * @param choice Ballot's csv formatted candidate preferences
     * @param startID Ballot's ID
     */
    IRBallot(String choice, int startID){
        super(choice, startID);
        String[] preferences = choice.split(",", -1);
        candidateRankIndex = new int[preferences.length];
        numCandidatesRanked = preferences.length;
        for (int i = 0; i < preferences.length; i++) {
            if(preferences[i].equals("")){
                preferences[i] = "-1";
                numCandidatesRanked--;
            }
            if(preferences[i].equals("1")){
                currentRankIndex = i;
                currentRank = 1;
            }

            candidateRankIndex[i] = Integer.parseInt(preferences[i]);
        }
    }

    /**
     * Updates both currentRank and currentRankIndex
     */
    public void updateCandidateIndex(){
        int nextRank = this.currentRank+1;
        boolean found = false;

        //Try's to find the ballot's next best candidate choice
        for(int i = 0; i < this.candidateRankIndex.length; i++){
            if(candidateRankIndex[i] == nextRank){
                currentRankIndex = i;
                currentRank = candidateRankIndex[i];
                found = true;
            }
        }

        //No next possible candidate, this ballot is now "dead"
        if(!found){
            currentRankIndex = -1;
        }
    }

    /**
     * Simple getter function for currentRank
     * @return currentRankIndex
     */
    public int getCurrentRankIndex(){
        return currentRankIndex;
    }

    /**
     * Simple getter function for ID
     * @return ID
     */
    public int getID(){
        return ID;
    }

    /**
     * Simple getter for numCandidatesRanked
     * @return the number of candidates the ballot has ranked
     */
    public int getNumCandidatesRanked(){ return numCandidatesRanked; }

    /**
     * Change currentRankIndex to -1 so this ballot is no longer ballot
     */
    public void invalidate(){ currentRankIndex = -1;}



}
