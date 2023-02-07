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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

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

    @Test
    public void testSmallPO(){
        File f = new File("testing/testCSVs/smallPO.csv");
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

        electionTest.runElection();

        Candidate[] candidates = electionTest.getCandidates();

        boolean valid = true;
        boolean candidateKennedy = false;
        boolean candidateTruman = false;
        boolean candidateBush = false;
        boolean candidateRubio = false;
        for (Candidate c : candidates){
            String name = c.getName();
            if (name.equals("Kennedy")){
                candidateKennedy = true;
            } else if (name.equals("Truman")){
                candidateTruman = true;
            } else if (name.equals("Bush")){
                candidateBush = true;
            } else if (name.equals("Rubio")){
                candidateRubio = true;
            } else{
                valid = false;
            }
        }
        assertTrue(candidateKennedy && candidateTruman && candidateBush && candidateRubio && valid);
        assertEquals(electionTest.getBallots().size(),10);
    }

    @Test
    public void testSmallPO2(){
        File f = new File("testing/testCSVs/smallPO2.csv");
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

        electionTest.runElection();

        Candidate[] candidates = electionTest.getCandidates();

        boolean valid = true;
        boolean candidateKennedy = false;
        boolean candidateTruman = false;
        boolean candidateBush = false;
        boolean candidateRubio = false;
        for (Candidate c : candidates){
            String name = c.getName();
            if (name.equals("Kennedy")){
                candidateKennedy = true;
            } else if (name.equals("Truman")){
                candidateTruman = true;
            } else if (name.equals("Bush")){
                candidateBush = true;
            } else if (name.equals("Rubio")){
                candidateRubio = true;
            } else{
                valid = false;
            }
        }
        assertTrue(candidateKennedy && candidateTruman && candidateBush && candidateRubio && valid);
        assertEquals(electionTest.getBallots().size(),15);
    }

    @Test
    public void testMediumPO(){
        File f = new File("testing/testCSVs/mediumPO.csv");
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

        electionTest.runElection();

        Candidate[] candidates = electionTest.getCandidates();

        boolean valid = true;
        boolean candidateKennedy = false;
        boolean candidateTruman = false;
        boolean candidateBush = false;
        boolean candidateRubio = false;
        boolean candidateSmith = false;
        for (Candidate c : candidates){
            String name = c.getName();
            if (name.equals("Kennedy")){
                candidateKennedy = true;
            } else if (name.equals("Truman")){
                candidateTruman = true;
            } else if (name.equals("Bush")){
                candidateBush = true;
            } else if (name.equals("Rubio")){
                candidateRubio = true;
            } else if (name.equals("Smith")){
                candidateSmith = true;
            } else{
                valid = false;
            }
        }
        assertTrue(candidateKennedy && candidateTruman && candidateBush && candidateRubio && candidateSmith && valid);
        assertEquals(electionTest.getBallots().size(),300);
    }

    @Test
    public void testLargePO(){
        File f = new File("testing/testCSVs/largePO.csv");
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

        electionTest.runElection();

        Candidate[] candidates = electionTest.getCandidates();

        boolean valid = true;
        boolean candidateKennedy = false;
        boolean candidateTruman = false;
        boolean candidateBush = false;
        boolean candidateRubio = false;
        boolean candidateSmith = false;
        for (Candidate c : candidates){
            String name = c.getName();
            if (name.equals("Kennedy")){
                candidateKennedy = true;
            } else if (name.equals("Truman")){
                candidateTruman = true;
            } else if (name.equals("Bush")){
                candidateBush = true;
            } else if (name.equals("Rubio")){
                candidateRubio = true;
            } else if (name.equals("Smith")){
                candidateSmith = true;
            } else{
                valid = false;
            }
        }
        assertTrue(candidateKennedy && candidateTruman && candidateBush && candidateRubio && candidateSmith && valid);
        assertEquals(electionTest.getBallots().size(),10000);
    }

    @Test
    public void testHugePO(){
        File f = new File("testing/testCSVs/hugePO.csv");
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(f);
        Election electionTest = new Election(fileArray);

        electionTest.runElection();

        Candidate[] candidates = electionTest.getCandidates();

        boolean valid = true;
        boolean candidateKennedy = false;
        boolean candidateTruman = false;
        boolean candidateBush = false;
        boolean candidateRubio = false;
        boolean candidateSmith = false;
        for (Candidate c : candidates){
            String name = c.getName();
            if (name.equals("Kennedy")){
                candidateKennedy = true;
            } else if (name.equals("Truman")){
                candidateTruman = true;
            } else if (name.equals("Bush")){
                candidateBush = true;
            } else if (name.equals("Rubio")){
                candidateRubio = true;
            } else if (name.equals("Smith")){
                candidateSmith = true;
            } else{
                valid = false;
            }
        }
        assertTrue(candidateKennedy && candidateTruman && candidateBush && candidateRubio && candidateSmith && valid);
        assertEquals(electionTest.getBallots().size(),100000);
    }

    @Test
    public void testMultipleSmallIR(){
        File location1 = new File("testing/testCSVs/smallIR.csv");
        File location2 = new File("testing/testCSVs/smallIR2.csv");
        File location3 = new File("testing/testCSVs/smallIR3.csv");

        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(location1);
        fileArray.add(location2);
        fileArray.add(location3);

        Election electionTest = new Election(fileArray);
        electionTest.runElection();
        //won't know the results yet for running these multiple IR files
        Candidate[] testCandidates = electionTest.getCandidates();
        int numWinners = 0;

        for (Candidate c : testCandidates) {
            if (!c.isEliminated()) {
                numWinners++;
                String name = c.getName();
                assertTrue(name.equals("Kleinberg (R)"));
            }
        }

        assertEquals(1, numWinners);
    }

    @Test
    public void testMultipleSmallandMediumIR(){
        File location1 = new File("testing/testCSVs/smallIR.csv");
        File location2 = new File("testing/testCSVs/mediumIR.csv");

        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(location1);
        fileArray.add(location2);

        Election electionTest = new Election(fileArray);
        electionTest.runElection();
        //won't know the results yet for running these multiple IR files

        Candidate[] testCandidates = electionTest.getCandidates();
        int numWinners = 0;

        for (Candidate c : testCandidates) {
            if (!c.isEliminated()) {
                numWinners++;
                String name = c.getName();
                assertTrue((name.equals("Kleinberg (R)")) || (name.equals("Chou (I)")));
            }
        }

        assertEquals(1, numWinners);
    }

    @Test
    public void testMultipleMediumOPL(){
        File location1 = new File("testing/testCSVs/mediumOPL.csv");
        File location2 = new File("testing/testCSVs/mediumOPLtie.csv");

        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(location1);
        fileArray.add(location2);

        Election electionTest = new Election(fileArray);
        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean FosterWinner = false;
        boolean JohnsonWinner = false;
        boolean JonesWinner = false;
        boolean BorgWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {

                String name = p.getCandidates().get(i).getName();
                numWinners++;

                if (name.equals("Foster")) {
                    FosterWinner = true;
                } else if (name.equals("Johnson")) {
                    JohnsonWinner = true;
                } else if (name.equals("Jones")) {
                    JonesWinner = true;
                } else if (name.equals("Borg")){
                    BorgWinner = true;
                } else {
                    valid = false;
                }
            }
        }
        assertTrue(FosterWinner && JohnsonWinner && JonesWinner && BorgWinner && valid);
        assertEquals(4, numWinners);
    }

    @Test
    public void testMultipleHugeSmallOPL(){
        File location2 = new File("testing/testCSVs/hugeOPL.csv");
        File location1 = new File("testing/testCSVs/smallOPL.csv");

        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(location1);
        fileArray.add(location2);

        Election electionTest = new Election(fileArray);
        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean FosterWinner = false;
        boolean DeutschWinner = false;
        boolean JonesWinner = false;
        boolean SmithWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {

                String name = p.getCandidates().get(i).getName();
                numWinners++;

                if (name.equals("Foster")) {
                    FosterWinner = true;
                } else if (name.equals("Deutsch")) {
                    DeutschWinner = true;
                } else if (name.equals("Jones")) {
                    JonesWinner = true;
                } else if (name.equals("Smith")){
                    SmithWinner = true;
                } else {
                    valid = false;
                }
            }
        }
        assertTrue(FosterWinner && SmithWinner && (JonesWinner || DeutschWinner) && valid);
        assertEquals(3, numWinners);
    }

    @Test
    public void testMultipleElection1(){
        File location1 = new File("testing/testCSVs/MultipleElection1/location1.csv");
        File location2 = new File("testing/testCSVs/MultipleElection1/location2.csv");
        File location3 = new File("testing/testCSVs/MultipleElection1/location3.csv");
        File location4 = new File("testing/testCSVs/MultipleElection1/location4.csv");
        File location5 = new File("testing/testCSVs/MultipleElection1/location5.csv");

        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(location1);
        fileArray.add(location2);
        fileArray.add(location3);
        fileArray.add(location4);
        fileArray.add(location5);
        Election electionTest = new Election(fileArray);
        electionTest.runElection();

        ArrayList<Party> testParties = electionTest.getParties();

        int numWinners = 0;
        boolean KennedyWinner = false;
        boolean BushWinner = false;
        boolean RubioWinner = false;
        boolean valid = true;

        for (Party p : testParties) {
            for (int i = 0; i < p.getSeatsAllocated(); i++) {

                String name = p.getCandidates().get(i).getName();
                numWinners++;

                if (name.equals("Kennedy")) {
                    KennedyWinner = true;
                } else if (name.equals("Bush")) {
                    BushWinner = true;
                } else if (name.equals("Rubio")) {
                    RubioWinner = true;
                } else {
                    valid = false;
                }
            }
        }
        assertTrue(KennedyWinner && BushWinner && RubioWinner && valid);
        assertEquals(3, numWinners);
    }

    @Test
    public void testMultiplePO(){
        File location1 = new File("testing/testCSVs/smallPO.csv");
        File location2 = new File("testing/testCSVs/smallPO2.csv");
        ArrayList<File> fileArray = new ArrayList<>();
        fileArray.add(location1);
        fileArray.add(location2);

        Election electionTest = new Election(fileArray);
        electionTest.runElection();
        Candidate[] candidates = electionTest.getCandidates();

        boolean valid = true;
        boolean candidateKennedy = false;
        boolean candidateTruman = false;
        boolean candidateBush = false;
        boolean candidateRubio = false;
        for (Candidate c : candidates){
            String name = c.getName();
            if (name.equals("Kennedy")){
                candidateKennedy = true;
            } else if (name.equals("Truman")){
                candidateTruman = true;
            } else if (name.equals("Bush")){
                candidateBush = true;
            } else if (name.equals("Rubio")){
                candidateRubio = true;
            } else{
                valid = false;
            }
        }
        assertTrue(candidateKennedy && candidateTruman && candidateBush && candidateRubio && valid);
        assertEquals(electionTest.getBallots().size(),25);
    }

    /*@Test
    public void testMultiTieIR(){


        float RosenWinner = 0;
        float KleinbergWinner = 0;
        float ChouWinner = 0;
        for(int i = 0; i<1000; i++) {
            File f = new File("testing/testCSVs/multiTieIR.csv");
            ArrayList<File> fileArray = new ArrayList<>();
            fileArray.add(f);
            Election electionTest = new Election(fileArray);

            electionTest.runElection();

            Candidate[] testCandidates = electionTest.getCandidates();
            for (Candidate c : testCandidates) {
                if (!c.isEliminated()) {
                    String name = c.getName();
                    if (name.equals("Rosen (D)")) {
                        RosenWinner++;
                    } else if (name.equals("Kleinberg (R)")) {
                        KleinbergWinner++;
                    } else if (name.equals("Chou (I)")) {
                        ChouWinner++;
                    }
                }
            }
        }
        RosenWinner = RosenWinner/1000;
        ChouWinner = ChouWinner/1000;
        KleinbergWinner = KleinbergWinner/1000;
        System.out.println(RosenWinner);
        System.out.println(ChouWinner);
        System.out.println(KleinbergWinner);
        assertTrue((.2<(RosenWinner)&& (RosenWinner)<.4)&&
                (.2<(ChouWinner)&& (ChouWinner)<.4)&&
                (.2<(KleinbergWinner)&& (KleinbergWinner)<.4));

    }
    @Test
    public void testMultiTieOPL(){


        float PikeWinner = 0;
        float DeutschWinner = 0;
        float SmithWinner = 0;
        for(int i = 0; i<1000; i++) {
            File f = new File("testing/testCSVs/multiTieOPL.csv");
            ArrayList<File> fileArray = new ArrayList<>();
            fileArray.add(f);
            Election electionTest = new Election(fileArray);

            electionTest.runElection();

            ArrayList<Party> testParties = electionTest.getParties();
            for (Party p : testParties) {
                for (int j = 0; j < p.getSeatsAllocated(); j++) {
                    String name = p.getCandidates().get(j).getName();
                    if (name.equals("Pike")) {
                        PikeWinner++;
                    } else if (name.equals("Deutsch")) {
                        DeutschWinner++;
                    } else if (name.equals("Smith")) {
                        SmithWinner++;
                    }
                }
            }
        }
        PikeWinner = PikeWinner/1000;
        DeutschWinner = DeutschWinner/1000;
        SmithWinner = SmithWinner/1000;
        System.out.println(PikeWinner);
        System.out.println(DeutschWinner);
        System.out.println(SmithWinner);
        assertTrue((.2<(PikeWinner)&& (PikeWinner)<.4)&&
                (.2<(DeutschWinner)&& (DeutschWinner)<.4)&&
                (.2<(SmithWinner)&& (SmithWinner)<.4));

    }*/

}
