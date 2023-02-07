//CandidateTest.java
//The purpose of this file is to test the public methods of the Candidate.java class by writing jUnit tests.
//This file was written by Tanner S and Varun R

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CandidateTest {

    @Test
    public void CandidateContsructorOPLTest(){
        Candidate testCandidate = new Candidate("Bob", "J");
        assertEquals("Bob", testCandidate.getName());
        assertEquals("J", testCandidate.getParty());
    }

    @Test
    public void CandidateContsructorIRTest(){
        Candidate testCandidate = new Candidate("Bob");
        assertEquals("Bob", testCandidate.getName());
    }

    @Test
    public void isGreaterThanTests(){

        Candidate cand1 = new Candidate("Name1", "Party1");
        Candidate cand2 = new Candidate("Name2", "Party2");

        cand1.incrementVote(new OPLBallot("1,,,,", 1));
        cand1.incrementVote(new OPLBallot("1,,,,", 2));
        cand1.incrementVote(new OPLBallot("1,,,,", 3));
        cand2.incrementVote(new OPLBallot(",1,,,", 4));
        cand2.incrementVote(new OPLBallot(",1,,,", 5));

        //test1: checks to see if greater than returns true when cand1 has more votes than cand2
        assertEquals(cand1.isGreaterThan(cand2), 1);

        //test2: checks to see if greater than returns false when cand2 is comparerd to cand1
        assertEquals(cand2.isGreaterThan(cand1), -1);
        cand2.incrementVote(new OPLBallot("1,,,,", 8));

        //test3: checks to see if greater than returns true when there is a tie, this would random normally
        assertEquals(cand1.isGreaterThan(cand2), 0);
    }
    @Test
    public void incrementVoteTest(){

        Candidate cand1 = new Candidate("Name1", "Party1");
        OPLBallot ballot1 = new OPLBallot("1,,,,", 1);

        cand1.incrementVote(ballot1);

        //test1: checks to see if incrementVote adds 1 to the numOfVotes attribute
        assertEquals(1, cand1.getNumVotes());
        ArrayList<Ballot> test1 = new ArrayList<>();
        test1.add(ballot1);

        //test2: tests if incrementVote adds a ballot to the currentBallot ArrayList
        assertEquals(test1, cand1.getCurrentBallots());
        Candidate cand2 = new Candidate("Name2", "Party2");
        cand2.incrementVote(null);

        //test3: checks for null handling
        assertEquals(0, cand2.getNumVotes());
    }

    @Test
    public void eliminateTest(){
        Candidate testCandidate = new Candidate("Bob");
        testCandidate.eliminate();
        assertEquals(-1, testCandidate.getNumVotes());
        assertTrue(testCandidate.isEliminated());
    }
}
