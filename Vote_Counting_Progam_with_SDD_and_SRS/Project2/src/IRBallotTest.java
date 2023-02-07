//IRBallotTest.java
//The purpose of this file is to test the public methods of the IRBallot.java class by writing jUnit tests.
//This file was written by Tanner S and Varun R

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IRBallotTest {

    @Test
    public void testIRBallotConstructor() {
        IRBallot testBallot = new IRBallot("1,3,4,2", 123);
        assertEquals(0, testBallot.getCurrentRankIndex());
    }

    @Test
    public void testGetNextCandidateIndex() {
        IRBallot testBallot = new IRBallot("1,3,4,2", 123);
        testBallot.updateCandidateIndex();
        assertEquals(3, testBallot.getCurrentRankIndex());
    }

    @Test
    public void testGetNextCandidateIndexOutOfOptions() {
        IRBallot testBallot = new IRBallot(",,,1", 123);
        testBallot.updateCandidateIndex();
        assertEquals(-1, testBallot.getCurrentRankIndex());
    }

    @Test
    public void testCandidateIndexMultipleUpdate() {

        IRBallot testBallot = new IRBallot("1,3,4,2", 123);

        assertEquals(0, testBallot.getCurrentRankIndex());

        testBallot.updateCandidateIndex();
        assertEquals(3, testBallot.getCurrentRankIndex());

        testBallot.updateCandidateIndex();
        assertEquals(1, testBallot.getCurrentRankIndex());

        testBallot.updateCandidateIndex();
        assertEquals(2, testBallot.getCurrentRankIndex());

        testBallot.updateCandidateIndex();
        assertEquals(-1, testBallot.getCurrentRankIndex());
    }

    @Test
    public void testCandidateIndexMultipleUpdateMissingRankings() {

        IRBallot testBallot = new IRBallot("1,,,2", 123);

        assertEquals(0, testBallot.getCurrentRankIndex());

        testBallot.updateCandidateIndex();
        assertEquals(3, testBallot.getCurrentRankIndex());

        testBallot.updateCandidateIndex();
        assertEquals(-1, testBallot.getCurrentRankIndex());
    }

    @Test
    public void testInvalidateBallots(){
        IRBallot testBallot = new IRBallot("1,2,3,4", 123);
        assertEquals(0, testBallot.getCurrentRankIndex());
        testBallot.invalidate();
        assertEquals(-1, testBallot.getCurrentRankIndex());

        IRBallot testBallot2 = new IRBallot(",1,,", 456);
        testBallot2.invalidate();
        assertEquals(-1, testBallot2.getCurrentRankIndex());
    }

    @Test
    public void testNumCandidatesRanked(){
        IRBallot testBallot = new IRBallot("1,2,3,4", 123);
        assertEquals(4, testBallot.getNumCandidatesRanked());

        IRBallot testBallot2 = new IRBallot(",1,,", 456);
        assertEquals(1, testBallot2.getNumCandidatesRanked());
    }

}