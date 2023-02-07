//PartyTest.java
//The purpose of this file is to test the public methods of the Party.java class by writing jUnit tests.
//This file was written by Tanner S and Varun R
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PartyTest {
    @Test
    public void testPartyConstructor(){

        Party testParty = new Party("test");

        assertEquals("test", testParty.getName());
        assertEquals(0, testParty.getNumVotes());
        assertEquals(0, testParty.getRemainder());
        assertEquals(0, testParty.getSeatsAllocated());
        assertEquals(0, testParty.getCandidates().size());
    }

    @Test
    public void testPartySumTotalPartyVotes(){

        Party party = new Party("test");

        Candidate candidate1 = new Candidate("test1");
        Candidate candidate2 = new Candidate("test2");

        candidate1.incrementVote(new OPLBallot("1,,,,",100 ));
        candidate1.incrementVote(new OPLBallot("1,,,,", 101));
        candidate2.incrementVote(new OPLBallot(",1,,,",102));

        party.addCandidate(candidate1);
        party.addCandidate(candidate2);

        int partyVoteTotal = party.sumTotalPartyVotes();

        assertEquals(3, partyVoteTotal);
    }

    @Test
    public void testPartySumTotalNoCandidates(){
        Party testParty = new Party("test");
        assertEquals(0, testParty.sumTotalPartyVotes());
    }

    @Test
    public void testAddSeats(){
        Party testParty = new Party("test");
        testParty.addSeats(5);
        assertEquals(5, testParty.getSeatsAllocated());
    }

    @Test
    public void testAddSeatsMultiple(){

        Party testParty = new Party("test");

        testParty.addSeats(5);
        testParty.addSeats(6);

        assertEquals(11, testParty.getSeatsAllocated());
    }

    @Test
    public void testAddCandidate(){

        Party testParty = new Party("test");

        Candidate candidate1 = new Candidate("test1");
        Candidate candidate2 = new Candidate("test2");

        testParty.addCandidate(candidate1);
        testParty.addCandidate(candidate2);

        ArrayList<Candidate> candidates = testParty.getCandidates();

        assertEquals(2, candidates.size());
        assertEquals("test1", candidates.get(0).getName());
        assertEquals("test2" , candidates.get(1).getName());
    }

    @Test
    public void testSetRemainder(){
        Party testParty = new Party("test");
        testParty.setRemainder(4);
        assertEquals(4, testParty.getRemainder());
    }

    @Test
    public void testSetRemainderUpdate(){
        Party testParty = new Party("test");
        testParty.setRemainder(3);
        testParty.setRemainder(4);
        assertEquals(4, testParty.getRemainder());
    }

    @Test
    public void testRankCandidates(){

        Party party = new Party("test");

        Candidate candidate1 = new Candidate("test1");
        Candidate candidate2 = new Candidate("test2");
        Candidate candidate3 = new Candidate("test3");

        candidate1.incrementVote(new OPLBallot("1,,,,",100 ));
        candidate1.incrementVote(new OPLBallot("1,,,,", 101));
        candidate3.incrementVote(new OPLBallot(",1,,,",102));

        party.addCandidate(candidate1);
        party.addCandidate(candidate2);
        party.addCandidate(candidate3);

        party.rankCandidates();

        ArrayList<Candidate> candidates = party.getCandidates();

        assertEquals(3, candidates.size());
        assertEquals("test1", candidates.get(0).getName());
        assertEquals("test3", candidates.get(1).getName());
        assertEquals("test2", candidates.get(2).getName());
    }

    @Test
    public void testGreaterRemain(){

        Party party1 = new Party("test");
        Party party2 = new Party("test2");

        party1.setRemainder(3);
        party2.setRemainder(4);

        assertTrue(party2.greaterRemain(party1, true));
        assertFalse(party1.greaterRemain(party2, true));
    }

    @Test
    public void testGreaterRemainTie(){

        Party party1 = new Party("test");
        Party party2 = new Party("test2");

        party1.setRemainder(3);
        party2.setRemainder(3);

        assertTrue(party2.greaterRemain(party1, true));
    }
}
