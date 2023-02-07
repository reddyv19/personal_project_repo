//POBallotTest.java
//The purpose of this file is to test the public methods of the POBallot.java
// class by writing jUnit tests.
//This file was written by Jonathan L

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class POBallotTest {
    @Test
    public void testPOBallotConstructor() {
        POBallot testBallot1 = new POBallot("1,,,", 123);
        POBallot testBallot2 = new POBallot(",,1,", 456);

        assertEquals(0, testBallot1.getCandidateIndex());
        assertEquals(123, testBallot1.getID());

        assertEquals(2, testBallot2.getCandidateIndex());
        assertEquals(456, testBallot2.getID());
    }

}
