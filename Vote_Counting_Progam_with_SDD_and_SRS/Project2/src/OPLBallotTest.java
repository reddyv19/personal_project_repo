//OPLBallotTest.java
//The purpose of this file is to test the public methods of the OPLBallotTest.java class by writing jUnit tests.
//This file was written by Tanner S and Varun R

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OPLBallotTest {
    @Test
    public void testOPLBallotConstructor(){
        OPLBallot testBallot = new OPLBallot("1,,,,", 123);
        assertEquals(0, testBallot.getCandidateIndex());
        OPLBallot testBallot2 = new OPLBallot(",,1,,", 123);
        assertEquals(2, testBallot2.getCandidateIndex());
    }
}