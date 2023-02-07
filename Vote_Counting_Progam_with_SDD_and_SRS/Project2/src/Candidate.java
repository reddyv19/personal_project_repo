// Candidate.java
// This file contains the Candidate object used in the Election class. Every candidate in an 
// OPL or IR csv file will have it's information stored using this object. 
// This file was written by Toby M and Jonathan L
import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a Candidate in an election
 */
public class Candidate implements Comparable<Candidate>{

    /**
     * The name of the party associated with this candidate
     */
    private String party;

    /**
     * The name of this candidate
     */
    private String name;

    /**
     * The number of votes this candidate currently has
     */
    private int numOfVotes;

    /**
     * If the candidate has been eliminated or not
     */
    private boolean isEliminated;

    /**
     * The ballots that are currently voting for this candidate
     */
    private ArrayList<Ballot> currentBallots;

    /**
     * The Random object used to generate random numbers to determine a tie
     */
    private Random random;

    /**
     * Constructor for Candidate for OPL
     * @param name The name of this candidate
     * @param party The party associated with this candidate
     */
    Candidate(String name, String party) {
        this.name = name;
        this.party = party;
        currentBallots = new ArrayList<>();
        random = new Random();
    }

    /**
     * Constructor for Candidate for IR
     * @param name The name of this candidate
     */
    Candidate(String name) {
        this.name = name;
        currentBallots = new ArrayList<>();
        random = new Random();
    }

    /**
     * Compare one candidate to another to see who has more votes
     * @param other The other candidate
     * @return If this candidate has more votes than other, or if there is a tie
     */
    public int isGreaterThan(Candidate other) {
        //check for tie
        if(numOfVotes > other.numOfVotes){
            return 1;
        }else if (numOfVotes < other.numOfVotes){
            return -1;
        }else{
            return 0;
        }

    }

    /**
     * Increment the number of votes of this candidate,
     * and add the ballot to the currentBallots arrayList
     * @param ballot The ballot to add to currentBallots
     */
    public void incrementVote(Ballot ballot) {
        if(ballot == null){
            return;
        }
        numOfVotes++;
        currentBallots.add(ballot);

    }

    /**
     * Getter for party
     * @return The party of the candidate
     */
    public String getParty() {
        return party;
    }

    /**
     * Getter for name
     * @return The name of the candidate
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for numVotes
     * @return numVotes
     */
    public int getNumVotes() {
        return numOfVotes;
    }

    /**
     * Return if the candidate is eliminated
     * @return If the candidate is eliminated
     */
    public boolean isEliminated() {
        return isEliminated;
    }

    /**
     * Eliminates the candidate from the election
     */
    public void eliminate(){
        this.isEliminated = true;
        this.numOfVotes = -1;
    }
    /**
     * Getter for the current ballot array
     * @return Current ballots array
     */
    public ArrayList<Ballot> getCurrentBallots() {
        return currentBallots;
    }

    /**
     * Compare a candidate to another candidate by their number of votes
     * @param other The other candidate to compare to
     * @return The difference between this candidate and the other candidate
     */
    @Override
    public int compareTo(Candidate other) {

        //check for a tie
        if (other.numOfVotes == numOfVotes) {
            random.setSeed(System.nanoTime());
            boolean choice = random.nextBoolean();
            if (choice) {
                return 1;
            } else {
                return -1;
            }
        }

        return other.numOfVotes - numOfVotes;
    }
}
