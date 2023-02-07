//ElectionTest.java
//The purpose of this file is to test the public methods of the Election.java class by writing jUnit tests.
//This file was written by Tanner S and Varun R

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElectionTest {

    @Test
    public void testSmallIR() {

        File f = new File("testing/testCSVs/smallIR.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        Candidate[] testCandidates = electionTest.getCandidates();
        for (Candidate c : testCandidates) {
            if (!c.isEliminated()) {
                assertEquals("Kleinberg (R)", c.getName());
            }
        }
    }

    @Test
    public void testSmallIR2() {

        File f = new File("testing/testCSVs/smallIR2.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        Candidate[] testCandidates = electionTest.getCandidates();
        int numWinners = 0;

        for (Candidate c : testCandidates) {
            if (!c.isEliminated()) {
                numWinners++;
                String name = c.getName();
                assertTrue((name.equals("Chou (I)")) || (name.equals("Royce (L)")));
            }
        }

        assertEquals(1, numWinners);
    }

    @Test
    public void testSmallMultiTieIR() {

        File f = new File("testing/testCSVs/multiTieIR.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        Candidate[] testCandidates = electionTest.getCandidates();

        int numWinners = 0;
        boolean RosenWinner = false;
        boolean KleinbergWinner = false;
        boolean ChouWinner = false;
        boolean valid = true;

        for (Candidate c : testCandidates) {
            if (!c.isEliminated()) {
                numWinners++;
                String name = c.getName();
                if (name.equals("Rosen (D)")) {
                    RosenWinner = true;
                } else if (name.equals("Kleinberg (R)")) {
                    KleinbergWinner = true;
                } else if (name.equals("Chou (I)")) {
                    ChouWinner = true;
                } else {
                    valid = false;
                }
            }
        }

        assertEquals(1, numWinners);
        assertTrue((RosenWinner || KleinbergWinner || ChouWinner) && valid);
    }

    @Test
    public void testSmallIR3() {

        File f = new File("testing/testCSVs/smallIR3.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        Candidate[] testCandidates = electionTest.getCandidates();
        for (Candidate c : testCandidates) {
            if (!c.isEliminated()) {
                assertEquals("Rosen (D)", c.getName());
            }
        }
    }

    @Test
    public void testSmallOPL() {

        File f = new File("testing/testCSVs/smallOPL.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean PikeWinner = false;
        boolean JonesWinner = false;
        boolean SmithWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {

                String name = p.getCandidates().get(i).getName();
                numWinners++;

                if (name.equals("Pike")) {
                    PikeWinner = true;
                } else if (name.equals("Jones")) {
                    JonesWinner = true;
                } else if (name.equals("Smith")) {
                    SmithWinner = true;
                } else {
                    valid = false;
                }
            }
        }

        assertTrue(PikeWinner && JonesWinner && SmithWinner && valid);
        assertEquals(3, numWinners);
    }

    @Test
    public void testMediumOPL() {

        File f = new File("testing/testCSVs/mediumOPL.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean PikeWinner = false;
        boolean JonesWinner = false;
        boolean FosterWinner = false;
        boolean BorgWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {
                String name = p.getCandidates().get(i).getName();
                numWinners++;
                if (name.equals("Pike")) {
                    PikeWinner = true;
                } else if (name.equals("Jones")) {
                    JonesWinner = true;
                } else if (name.equals("Foster")) {
                    FosterWinner = true;
                } else if (name.equals("Borg")) {
                    BorgWinner = true;
                } else {
                    valid = false;
                }
            }
        }
        assertTrue(PikeWinner && JonesWinner && FosterWinner && BorgWinner && valid);
        assertEquals(4, numWinners);
    }

    @Test
    public void testMediumOPLtie() {

        File f = new File("testing/testCSVs/mediumOPLtie.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean JonesWinner = false;
        boolean FosterOrJohnsonWinner = false;
        boolean BorgWinner = false;
        boolean SmithWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {

                String name = p.getCandidates().get(i).getName();
                numWinners++;

                if (name.equals("Jones")) {
                    JonesWinner = true;
                } else if (name.equals("Johnson") || name.equals("Foster")) {
                    FosterOrJohnsonWinner = true;
                } else if (name.equals("Borg")) {
                    BorgWinner = true;
                } else if (name.equals("Smith")) {
                    SmithWinner = true;
                } else {
                    valid = false;
                }
            }
        }
        assertTrue(JonesWinner && FosterOrJohnsonWinner && BorgWinner && SmithWinner && valid);
        assertEquals(4, numWinners);
    }

    @Test
    public void testLargeOPL() {

        File f = new File("testing/testCSVs/largeOPL.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean JonesWinner = false;
        boolean FosterWinner = false;
        boolean JohnsonWinner = false;
        boolean DeutschWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {

                String name = p.getCandidates().get(i).getName();
                numWinners++;

                if (name.equals("Johnson")) {
                    JohnsonWinner = true;
                } else if (name.equals("Jones")) {
                    JonesWinner = true;
                } else if (name.equals("Foster")) {
                    FosterWinner = true;
                } else if (name.equals("Deutsch")) {
                    DeutschWinner = true;
                } else {
                    valid = false;
                }
            }
        }
        assertTrue(JohnsonWinner && JonesWinner && FosterWinner && DeutschWinner && valid);
        assertEquals(4, numWinners);
    }

    @Test
    public void testHugeOPL() {

        File f = new File("testing/testCSVs/hugeOPL.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean FosterWinner = false;
        boolean DeutschWinner = false;
        boolean SmithWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {

                String name = p.getCandidates().get(i).getName();
                numWinners++;

                if (name.equals("Smith")) {
                    SmithWinner = true;
                } else if (name.equals("Foster")) {
                    FosterWinner = true;
                } else if (name.equals("Deutsch")) {
                    DeutschWinner = true;
                } else {
                    valid = false;
                }
            }
        }
        assertTrue(SmithWinner && FosterWinner && DeutschWinner && valid);
        assertEquals(3, numWinners);
    }

    @Test
    public void testPartyTieOPL() {

        File f = new File("testing/testCSVs/partyTieOPL.csv");
        Election electionTest = new Election(f);

        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean PikeWinner = false;
        boolean DeutschWinner = false;
        boolean SmithWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {

                String name = p.getCandidates().get(i).getName();
                numWinners++;

                if (name.equals("Smith")) {
                    SmithWinner = true;
                } else if (name.equals("Pike")) {
                    PikeWinner = true;
                } else if (name.equals("Deutsch")) {
                    DeutschWinner = true;
                } else {
                    valid = false;
                }
            }
        }
        assertTrue(SmithWinner && (PikeWinner || DeutschWinner) && valid);
        assertEquals(2, numWinners);
    }

}