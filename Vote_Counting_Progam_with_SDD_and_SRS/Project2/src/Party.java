// Party.java
// This file contains the Party object used in the Election class. Every party in an 
// OPL csv file will have it's information stored using this object (primarily which candidates are a part of the party). 
// This file was written by Toby M and Jonathan L
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Represents a Party in an election
 */
public class Party {

    /**
     * The name of the party
     */
    private String party;

    /**
     * The number of seats allocated to the party
     */
    private int seatsAllocated;

    /**
     * The number of votes the party has
     */
    private int numOfVotes;

    /**
     * The remainder of votes that did not meet the threshold(?)
     */
    private int remainder;

    /**
     * All the candidates associated with this party
     */
    private ArrayList<Candidate> candidates;

    /**
     * The Random object used to generate random numbers to determine a tie
     */
    private Random random;

    /**
     * Constructor for a party
     * @param name The name of the party
     */
    Party(String name) {
        party = name;
        candidates = new ArrayList<>();
        random = new Random();
    }

    /**
     * Determine if this party has a greater number of votes than another
     * @param other The other party to compare to
     * @return If this party has more votes than other, or 0 is there is a tie
     */
    public int greaterRemain(Party other) {

        //check for tie
        if(remainder > other.remainder){
            return 1;
        }else if (remainder < other.remainder){
            return -1;
        }else{
            return 0;
        }
    }

    /**
     * Sum the votes of all candidates in this party
     * @return The sum of all votes
     */
    public int sumTotalPartyVotes() {

        //reset numOfVotes
        numOfVotes = 0;

        //increment numOfVotes by every candidate's votes
        for (Candidate candidate:candidates) {
            numOfVotes += candidate.getNumVotes();
        }

        return numOfVotes;
    }

    /**
     * Add a candidate to this party
     * @param candidate The candidate to add
     */
    public void addCandidate(Candidate candidate) {

        //add candidate to candidates
        candidates.add(candidate);

    }

    /**
     * Add a number of seats to this party
     * @param seats The number of seats to add
     */
    public void addSeats(int seats) {

        seatsAllocated += seats;

    }

    /**
     * Rank the candidates in the party
     */
    public void rankCandidates() {
        Collections.sort(candidates);
    }

    /**
     * Set the remainder of votes this party has
     * @param remainder The remainder of votes
     */
    public void setRemainder(int remainder) {

        this.remainder = remainder;

    }

    /**
     * Getter for party
     * @return The name of the party
     */
    public String getName() {
        return party;
    }

    /**
     * Getter for seatsAllocated
     * @return seatsAllocated
     */
    public int getSeatsAllocated() {
        return seatsAllocated;
    }

    /**
     * Getter for numVotes
     * @return numVotes
     */
    public int getNumVotes() {
        return numOfVotes;
    }

    /**
     * Getter for remainder
     * @return remainder
     */
    public int getRemainder() {
        return remainder;
    }

    /**
     * Getter for candidates array
     * @return Candidates array
     */
    public ArrayList<Candidate> getCandidates() {
        return candidates;
    }

}
