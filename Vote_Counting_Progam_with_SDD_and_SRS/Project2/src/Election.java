// Election.java
// This file contains the Election object. It determines the outcome of both an IR and OPL election.
// It will also generate two files, a media file and an audit file, to show how the election progressed.
// This file was written by Toby M and Jonathan L
import java.io.*;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.lang.Math;
import java.util.Random;
/**
 * Represents an election
 */
public class Election {

    //I changed this to File, since we open the file in Main and pass in the actual file object here
    /**
     * The election file
     */
    private File electionFile;

    /**
     * An ArrayList containing all election files
     */
    private ArrayList<File> electionFiles;

    /**
     * The scanner that reads the file
     */
    private Scanner fileReader;

    /**
     * The audit file
     */
    private File auditFile;

    /**
     * The media file
     */
    private File mediaFile;

    /**
     * The total number of seats to fill in the election
     */
    private int seatsTotal;

    /**
     * The number of seats left to fill in the election
     */
    private int seatsRemain;

    /**
     * The total number of votes cast in the election
     */
    private int totalVotes;

    /**
     * The total number of candidates in the election
     */
    private int totalCandidates;

    private Random random;

    private ArrayList<Integer> tieDeciderList;

    /**
     * The number of candidates left in the election
     * The number of candidates that have not been eliminated
     */
    private int candidatesLeft;

    /**
     * All of the candidates in the election
     */
    private Candidate[] candidatesArray;

    /**
     * All of the parties in the election
     */
    private ArrayList<Party> partyArray;

    /**
     * All of the ballots in the election
     */
    private ArrayList<Ballot> ballots;

    /**
     * The type of election
     * IR or OPL
     */
    private String type;

    /**
     * Constructor for an election taking in multiple files
     * @param files The election files
     */
    Election(ArrayList<File> files) {

        electionFiles = files;

        random = new Random();

        //get the first election file for all election information about candidates/parties/election type
        electionFile = electionFiles.get(0);
        tieDeciderList = new ArrayList<>();

        //make a scanner for the file
        try {
            fileReader = new Scanner(electionFile);
        } catch (FileNotFoundException e) {
            System.err.println("File invalid or not found");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Redistribute votes from an eliminated candidate to the ballot's next choice
     * @param candidateIndex The index of the eliminated candidate
     */
    private void redistributeVotes(int candidateIndex) {

        //go through all votes of the candidate to be eliminated
        for (int i = 0; i < candidatesArray[candidateIndex].getNumVotes(); i++){

            //get the ballot voting for the candidate
            IRBallot temp = (IRBallot) candidatesArray[candidateIndex].getCurrentBallots().get(i);

            //update the ballot to move on to the next vote
            temp.updateCandidateIndex();

            //if there is a next vote to move on to
            if (temp.getCurrentRankIndex() != -1) {

                //while the next vote is not already eliminated
                while (candidatesArray[temp.getCurrentRankIndex()].isEliminated()) {

                    //move on to the next vote
                    temp.updateCandidateIndex();

                    //if there is no vote to move onto, stop
                    if (temp.getCurrentRankIndex() == -1) {
                        break;
                    }
                }
            }

            //check if the next candidate exists, and update the vote if they do
            int nextCandidate = temp.getCurrentRankIndex();
            if (nextCandidate != -1){
                candidatesArray[nextCandidate].incrementVote(temp);
            }
        }
        //eliminate the candidate that was redistributed
        candidatesArray[candidateIndex].eliminate();
        candidatesLeft--;
    }

    /**
     * Display the results of the election
     * @param runTime The time it took the election to run in milliseconds
     */
    public void displayWinners(long runTime) {

        //convert runTime to seconds
        double runTimeSeconds = runTime/1000000000.;

        System.out.format("Election completed in %f seconds%n", runTimeSeconds);

        System.out.println("Elected Official(s):");


        if (type.equals("IR")) {

            //for each candidate, print their information if they are not eliminated
            for (Candidate candidate : candidatesArray) {
                if (!candidate.isEliminated()) {
                    System.out.format("[%s]%n", candidate.getName());
                }
            }

        } else if(type.equals("OPL"))  {

            //for each party, print all candidates that were elected within the party
            for (Party party: partyArray) {
                for (int i = 0; i < party.getSeatsAllocated(); i++) {
                    System.out.format("[%s, %s]%n", party.getCandidates().get(i).getName(), party.getName());
                }
            }

        }
        else{
            System.out.println("N/A");
        }

        System.out.format("The media file will be saved under /%s%n", mediaFile.getName()); //??? Idk if this works
        System.out.format("The audit file will be saved under /%s%n", auditFile.getName()); //Same with this one

    }

    /**
     * Update the audit file with the current state of the election
     * @param round The round of voting
     */
    private void updateAudit(int round) throws IOException {

        //create a fileWriter and printWriter to write to the audit file
        FileWriter fileWriter = new FileWriter(auditFile.getName(),true);
        PrintWriter pw = new PrintWriter(fileWriter);
        pw.print("");

        //OPL election
        if (type.equals("OPL")){

            //special stuff for the initial update
            if (round == 0){

                //get the current date
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String time = formatter.format(date);

                pw.printf("This is the audit file for the OPL election on %s %n",time);
                pw.println("Parties and Candidates:");

                //print the total votes for each party and the candidates within the party
                for (Party party : partyArray){
                    pw.printf("%s Total Votes: %d %n",party.getName(), party.getNumVotes());
                    for (Candidate candidate : party.getCandidates()){
                        pw.printf("[%s, %s]: %d %n", candidate.getName(), candidate.getParty(), candidate.getNumVotes());
                    }
                    pw.println();
                }

                //print the total votes in the election
                pw.printf("Total Votes: %d %n",totalVotes);

                //print the ballot IDs for all candidates
                pw.println("Ballot locations:");
                for (Candidate candidate : candidatesArray){
                    if (!candidate.isEliminated()){
                        pw.printf("[%s, %s]%n", candidate.getName(), candidate.getParty());
                        for (int i = 0; i<candidate.getNumVotes(); i++){
                            pw.printf("ID: %d %n",candidate.getCurrentBallots().get(i).getID());
                        }
                    }
                    pw.println();
                }

                //print initial seats allocated to each party
                pw.println("Initial Seats Allocated:");
                for (Party party : partyArray){
                    pw.printf("%s: %d %n",party.getName(),party.getSeatsAllocated());
                }
                pw.println();

                //print the initial remainder for each party
                pw.println("Initial Remainders:");
                for (Party party : partyArray){
                    pw.printf("%s: %d %n",party.getName(),party.getRemainder());
                }
                pw.println();
            }

            //print the allocated seats for each party in the current round
            pw.printf("Round %d Allocated Seats: %n", round);
            for (Party party : partyArray){
                pw.printf("%s: %d %n",party.getName(),party.getSeatsAllocated());
            }
            pw.println();

            //print the current remainder for each party in the current round
            pw.printf("Round %d Remainders: %n", round);
            for (Party party : partyArray){
                pw.printf("%s: %d %n",party.getName(),party.getRemainder());
            }
            pw.println();


        }
        //IR election
        else {

            //special stuff for the initial update
            if (round == 1){

                //get the current date
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String time = formatter.format(date);

                pw.printf("This is the audit file for the IR election on %s %n",time);

                //print all the candidates
                pw.println("Candidates: ");
                for (Candidate candidate : candidatesArray){
                    pw.printf("[%s]%n", candidate.getName());
                }
                pw.println();

                //print the total amount of votes
                pw.printf("Total Votes: %d %n",totalVotes);
            }

            //print the ballot IDs for each candidate in the current round
            pw.printf("Ballot locations for Round %d:%n",round);
            for (Candidate candidate : candidatesArray){
                if (!candidate.isEliminated()){
                    pw.printf("[%s]%n", candidate.getName());
                    for (int i = 0; i<candidate.getNumVotes(); i++){
                        pw.printf("ID: %d%n",candidate.getCurrentBallots().get(i).getID());
                    }
                }
                pw.println();
            }

            //print the number of votes for each non-eliminated candidate in the current round
            pw.printf("Round %d totals:%n",round);
            for (Candidate candidate : candidatesArray){
                if (!candidate.isEliminated()){
                    pw.printf("[%s]: %d %n", candidate.getName(),candidate.getNumVotes());
                }
                pw.println();
            }

            //print all the candidates who have been eliminated up to this point
            pw.printf("Candidates Eliminated as of round %d: %n",round);
            for (Candidate candidate : candidatesArray){
                if (candidate.isEliminated()){
                    pw.printf("[%s]%n", candidate.getName());
                }
            }
            pw.println();
        }

        //close fileWriter and printWriter
        pw.close();
        fileWriter.close();
    }

    /**
     * End and close the audit file
     */
    private void endAudit() throws IOException {

        //create a fileWriter and printWriter to write to the audit file
        FileWriter fileWriter = new FileWriter(auditFile.getName(),true);
        PrintWriter pw = new PrintWriter(fileWriter);
        pw.print("");

        //OPL Election
        if (type.equals("OPL")){

            //print each party and the number of seats allocated to that party
            pw.println("The election has now concluded, here are the final seat tally's: ");
            for (Party party : partyArray){
                pw.printf("%s: %d %n",party.getName(),party.getSeatsAllocated());
            }
            pw.println();

            //print all elected officials for each party
            pw.println("Based on the rankings within each party, here are the elected officials:");
            for (Party party : partyArray){
                pw.printf("%s: %n",party.getName());
                for (int i =0; i < party.getSeatsAllocated(); i++){
                    Candidate candidate = party.getCandidates().get(i);
                    pw.printf("\t[%s, %s]%n", candidate.getName(), candidate.getParty());
                }
                pw.println();
            }
        }
        //IR Election
        else {

            //print all the candidates who aren't eliminated, these are the winners
            pw.println("The election has now concluded, the winner of the election is:");
            for (Candidate candidate : candidatesArray){
                if(!candidate.isEliminated()){
                    pw.printf("[%s] won with %d votes! %n", candidate.getName(),candidate.getNumVotes());
                }
            }
        }

        //close the printWriter and fileWriter
        pw.close();
        fileWriter.close();
    }

    /**
     * Run the election process
     */
    public void runElection(){

        //Current time to be used for Audit and Media file names
        String firstLine = fileReader.nextLine();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        Date date = new Date(System.currentTimeMillis());

        //Create audit file
        String auditName = "audit"+formatter.format(date)+".txt";
        auditFile = new File(auditName);

        //Create the media file
        String mediaName = "media" + formatter.format(date)+".txt";
        mediaFile = new File(mediaName);

        //Run either an OPL election or an IR election
        if (firstLine.equals("OPL")) {
            type = "OPL";
            OPLInit();
            runOPL();
        } else if (firstLine.equals("IR")) {
            type = "IR";
            IRInit();
            runIR();
        }
        else if (firstLine.equals("PO")){
            type = "PO";
            POInit();
        }
        else{
            System.out.printf("Ballot Type %s not valid\n", firstLine);
            System.exit(1);
        }
    }

    /**
     * Run an IR election
     */
    private void runIR(){

        int round = 1;

        //give candidates their votes and ballots
        for (int i = 0; i < ballots.size(); i++){
            IRBallot temp =  (IRBallot) ballots.get(i);
            int currCandidate = temp.getCurrentRankIndex();
            if (currCandidate != -1) {
                candidatesArray[currCandidate].incrementVote(temp);
            }
        }
        
        //If only two candidates remain, popularity wins
        while(candidatesLeft > 2){
            
            //Update the Media and Audit Files
            try {
                updateAudit(round);
                updateMedia(round);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Check if a candidate has a majority.
            for (int i = 0; i < totalCandidates; i++) {
                if (candidatesArray[i].getNumVotes() > totalVotes / 2) {
                    //Candidate i has a majority and thus wins the election
                    eliminateAll(i);
                    
                    //Update Media and Audit files
                    try {
                        endAudit();
                        endMedia();
                    } catch (IOException e) {
                        System.err.println("updateAudit or updateMedia failed");
                        e.printStackTrace();
                    }
                    return;
                }
            }

            //no candidate has a majority
            round++;
            int minCandidate = -1;

            //Eliminate the minimum Candidate
            for (int i = 0; i < totalCandidates; i++) {
                if (!candidatesArray[i].isEliminated()) {
                    if (minCandidate != -1) {
                        //decide tie here

                        if (candidatesArray[minCandidate].isGreaterThan(candidatesArray[i])==1) {
                            minCandidate = i;
                            tieDeciderList.clear();
                            tieDeciderList.add(i);
                        }else if (candidatesArray[minCandidate].isGreaterThan(candidatesArray[i])==0){
                            tieDeciderList.add(i);
                        }

                    } else {
                        //find an initial minimum candidate
                        tieDeciderList.add(i);
                        minCandidate = i;
                    }
                }

            }
            int loser = 0;
            if(tieDeciderList.size()>1) {
                loser = decideTie(tieDeciderList.size());
                minCandidate = tieDeciderList.get(loser);
            }
            tieDeciderList.clear();
            redistributeVotes(minCandidate);
        }

        //two candidates remain
        Candidate can1 = null;
        Candidate can2 = null;
        for (int i = 0; i < totalCandidates; i++){
            if (!candidatesArray[i].isEliminated()){//We need either an eliminate all function or to return the winning candidate
                if (can1 == null){
                    can1 = candidatesArray[i];
                }
                else{
                    can2 = candidatesArray[i];
                }
            }
        }
        try {
            updateAudit(round);
            updateMedia(round);
        } catch (IOException e) {
            System.err.println("updateAudit or updateMedia failed");
            e.printStackTrace();
        }

        assert can1 != null;
        assert can2 != null;
        //decide tie here
        if (can1.isGreaterThan(can2)==1){
            can2.eliminate();
        } else if(can1.isGreaterThan(can2)==-1){
            can1.eliminate();
        }else{
            if(decideTie(2)==0){
                can1.eliminate();
            }else{
                can2.eliminate();
            }
        }
        try {
            endAudit();
            endMedia();
        } catch (IOException e) {
            System.err.println("updateAudit or updateMedia failed");
            e.printStackTrace();
        }

    }

    /**
     * Run an OPL election
     */
    private void runOPL() {

        //give candidates their votes and ballots
        for (int i = 0; i < totalVotes; i++){
            OPLBallot temp =  (OPLBallot) ballots.get(i);
            int currCandidate = temp.getCandidateIndex();
            candidatesArray[currCandidate].incrementVote(temp);
        }

        int round = 0;
        int quota = totalVotes/seatsTotal;

        //allocate initial seats to parties
        for (Party party : partyArray) {
            int votes = party.sumTotalPartyVotes();
            party.addSeats(votes / quota);
            party.setRemainder(votes % quota);
            seatsRemain -= party.getSeatsAllocated();
            party.rankCandidates();
        }

        //Update media and audit files
        try {
            updateAudit(round);
            updateMedia(round);
        } catch (IOException e) {
            System.err.println("updateAudit or updateMedia failed");
            e.printStackTrace();
        }

        //Allocate remainder of the seats
        while(seatsRemain > 0){
            round++;
            int maxRemainder = 0;

            //set i to index of party with greatest remainder
            for (int i = 0; i < partyArray.size(); i++){
                //decide tie here
                if (partyArray.get(i).greaterRemain(partyArray.get(maxRemainder))==1){
                    maxRemainder = i;
                    tieDeciderList.clear();
                    tieDeciderList.add(i);
                }else if(partyArray.get(i).greaterRemain(partyArray.get(maxRemainder))==0){
                    tieDeciderList.add(i);
                }
            }
            int winner;
            if(tieDeciderList.size()>1) {
                winner = decideTie(tieDeciderList.size());
                maxRemainder = tieDeciderList.get(winner);
            }

            tieDeciderList.clear();
            //Party with greatest remainder gets an additional seat
            partyArray.get(maxRemainder).setRemainder(-1);
            partyArray.get(maxRemainder).addSeats(1);
            seatsRemain--;

            //Update media and audit files
            try {
                updateAudit(round);
                updateMedia(round);
            } catch (IOException e) {
                System.err.println("updateAudit or updateMedia failed");
                e.printStackTrace();
            }

        }

        //End media and audit files
        try {
            endAudit();
            endMedia();
        } catch (IOException e) {
            System.err.println("updateAudit or updateMedia failed");
            e.printStackTrace();
        }

    }

    /**
     * Initialize an IR election
     */
    private void IRInit(){

        //Initializes Candidate array
        totalCandidates = fileReader.nextInt();
        candidatesLeft = totalCandidates;
        candidatesArray = new Candidate[totalCandidates];

        //Initializes all of the Candidates
        fileReader.nextLine();
        String[] candidateNames = fileReader.nextLine().split(",");
        for (int i =0; i < totalCandidates; i++){
            candidatesArray[i] = new Candidate(candidateNames[i]);
        }

        //this will be incremented with each ballot in all files
        totalVotes = 0;
        fileReader.nextLine();

        int threshold = (int) Math.ceil( (double) (totalCandidates / 2));
        //Initializes all of the Ballots
        ballots = new ArrayList<>();

        //count ballots across all files for their unique IDs
        int ballotCountID = 0;

        //Go through all election files
        for (File file: electionFiles) {

            Scanner ballotScanner = null;

            //make a scanner from the current election file
            try {
                ballotScanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                System.err.println("Election file not found");
                e.printStackTrace();
                System.exit(1);
            }

            //skip the lines at the beginning
            ballotScanner.nextLine();
            ballotScanner.nextLine();
            ballotScanner.nextLine();

            //get the number of votes in this election file
            int currentVoteCount = ballotScanner.nextInt();

            //skip one more line to get to the ballots
            ballotScanner.nextLine();

            //for all the total votes in this election file (also the number of ballots)
            for (int i = 0; i < currentVoteCount; i++) {
                String rankings = ballotScanner.nextLine();
                IRBallot temp =  new IRBallot(rankings, ballotCountID);

                ballotCountID++;

                //Ensure that the ballot has enough candidates ranked to be counted
                if (temp.getNumCandidatesRanked() < threshold){
                    temp.invalidate();
                } else {
                    //only increment totalVotes if the vote is actually valid and counted
                    totalVotes++;
                }
                ballots.add(temp);
            }

        }
    }

    /**
     * Initialize an OPL election
     */
    private void OPLInit() {
        //Initializes Candidate array
        totalCandidates = fileReader.nextInt();
        candidatesArray = new Candidate[totalCandidates];

        partyArray = new ArrayList<>();
        fileReader.nextLine();

        //Initializes all of the Candidates and Parties
        String[] candidateNames = fileReader.nextLine().split(",");
        for (int i =0; i < totalCandidates*2; i+=2){
            String party = candidateNames[i+1].substring(0,1);
            String name = candidateNames[i].substring(1);
            candidatesArray[i/2] = new Candidate(name,party);
            boolean exist = false;
            for (Party value : partyArray) {
                if (value.getName().equals(party)) {
                    exist = true;
                    value.addCandidate(candidatesArray[i / 2]);
                }
            }
            //Party has not been created
            if(!exist){
                partyArray.add(new Party(party));
                partyArray.get(partyArray.size() - 1).addCandidate(candidatesArray[i/2]);
            }
        }
        //Initializes the amount of seats available
        seatsTotal = fileReader.nextInt();
        seatsRemain = seatsTotal;

        //this will be incremented with each ballot in all files
        totalVotes = 0;
        fileReader.nextLine();

        //Initializes all of the ballots and stores them in the ballot array
        ballots = new ArrayList<>();

        //count ballots across all files for their unique IDs
        int ballotCountID = 0;

        //Go through all election files
        for (File file: electionFiles) {

            Scanner ballotScanner = null;

            //make a scanner from the current election file
            try {
                ballotScanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                System.err.println("Election file not found");
                e.printStackTrace();
                System.exit(1);
            }

            //skip the lines at the beginning
            ballotScanner.nextLine();
            ballotScanner.nextLine();
            ballotScanner.nextLine();
            ballotScanner.nextLine();

            //get the number of votes in this election file
            int currentVoteCount = ballotScanner.nextInt();

            //skip one more line to get to the ballots
            ballotScanner.nextLine();

            //for all the total votes in this election file (also the number of ballots)
            for (int i = 0; i < currentVoteCount; i++) {
                String rankings = ballotScanner.nextLine();
                ballots.add(new OPLBallot(rankings, ballotCountID));

                ballotCountID++;
                totalVotes++;
            }

        }
    }

    /**
     * Initialize an PO election
     */
    private void POInit(){
        //Initializes Candidate array
        totalCandidates = fileReader.nextInt();
        candidatesLeft = totalCandidates;
        candidatesArray = new Candidate[totalCandidates];
        partyArray = new ArrayList<>();

        fileReader.nextLine();
        //Initializes all of the Candidates and Parties
        String[] candidateNames = fileReader.nextLine().split(",");
        for (int i =0; i < totalCandidates*2; i+=2){
            String party = candidateNames[i+1].substring(0,1);
            String name = candidateNames[i].substring(1);
            candidatesArray[i/2] = new Candidate(name,party);
            boolean exist = false;
            for (Party value : partyArray) {
                if (value.getName().equals(party)) {
                    exist = true;
                    value.addCandidate(candidatesArray[i / 2]);
                }
            }
            //Party has not been created
            if(!exist){
                partyArray.add(new Party(party));
                partyArray.get(partyArray.size() - 1).addCandidate(candidatesArray[i/2]);
            }
        }

        //this will be incremented with each ballot in all files
        totalVotes = 0;
        fileReader.nextLine();

        //Initializes all of the Ballots
        ballots = new ArrayList<>();

        //count ballots across all files for their unique IDs
        int ballotCountID = 0;

        //Go through all election files
        for (File file: electionFiles) {

            Scanner ballotScanner = null;

            //make a scanner from the current election file
            try {
                ballotScanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                System.err.println("Election file not found");
                e.printStackTrace();
                System.exit(1);
            }

            //skip the lines at the beginning
            ballotScanner.nextLine();
            ballotScanner.nextLine();
            ballotScanner.nextLine();

            //get the number of votes in this election file
            int currentVoteCount = ballotScanner.nextInt();

            //skip one more line to get to the ballots
            ballotScanner.nextLine();

            //for all the total votes in this election file (also the number of ballots)
            for (int i = 0; i < currentVoteCount; i++) {
                String rankings = ballotScanner.nextLine();
                ballots.add(new POBallot(rankings, ballotCountID));

                ballotCountID++;
                totalVotes++;
            }

        }
    }

    /**
     * Write the media file
     */
    private void endMedia() throws IOException {

        FileWriter fileWriter = new FileWriter(mediaFile.getName(), true);
        PrintWriter pw = new PrintWriter(fileWriter);
        pw.print("");

        if (type.equals("OPL")){

            pw.println("Election Results:");

            //party seats allocated

            pw.println("Seats Allocated:");
            for (Party party : partyArray){
                pw.printf("%s: %d%n",party.getName(),party.getSeatsAllocated());
            }

            //party total votes
            pw.println("Total Party Votes");
            for (Party party : partyArray) {
                pw.printf("%s: %d%n", party.getName(), party.getNumVotes());
            }

            //elected officials
            pw.println("Elected Officials:");
            for (Party party : partyArray){

                pw.printf("%s:%n",party.getName());

                for (int i =0; i < party.getSeatsAllocated(); i++){
                    Candidate candidate = party.getCandidates().get(i);
                    pw.printf("[%s, %s]%n", candidate.getName(), candidate.getParty());
                }
            }

        } else {

            pw.println("Election Results:");

            for (Candidate candidate : candidatesArray){
                if(!candidate.isEliminated()){
                    pw.printf("[%s]%n won with %d votes!", candidate.getName(), candidate.getNumVotes());
                }
            }

        }

        pw.close();
        fileWriter.close();

    }

    /**
     * Update the media file with the current state of the election
     * @param round The current round of voting
     * @throws IOException If the file does not exist
     */
    private void updateMedia(int round) throws IOException {

        //Will add onto the existing media file
        FileWriter fileWriter = new FileWriter(mediaFile.getName(),true);
        PrintWriter pw = new PrintWriter(fileWriter);
        pw.print("");

        if (type.equals("OPL")){

            //Special case for the initial part of a media file
            if (round == 0){
                
                //Media file header
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String time = formatter.format(date);

                pw.printf("Media File for OPL election on %s%n",time);
                pw.print("Candidates: ");

                //Initial ballot's and seat allocations:
                for (Party party : partyArray){

                    for (Candidate candidate : party.getCandidates()){
                        pw.printf("[%s, %s] ", candidate.getName(), candidate.getParty());
                    }

                }

                pw.println();
                pw.println();
                pw.printf("Total Votes: %d%n%n", totalVotes);

                pw.printf("Seats Available: %d%n%n", seatsTotal);

                pw.println("Initial Seats Allocated:");
                for (Party party : partyArray){
                    pw.printf("%s: %d%n",party.getName(),party.getSeatsAllocated());
                }
                pw.println();

                pw.println("Initial Remainders:");
                for (Party party : partyArray){
                    pw.printf("%s: %d%n",party.getName(),party.getRemainder());
                }
                pw.println();
            }
            
            //Seat allocation for every round:
            pw.printf("Round %d Allocated Seats:%n", round);

            for (Party party : partyArray) {
                pw.printf("%s: %d%n", party.getName(), party.getSeatsAllocated());
            }
            pw.println();

            pw.printf("Round %d Remainders:%n", round);
            for (Party party : partyArray){
                pw.printf("%s: %d%n",party.getName(),party.getRemainder());
            }
            pw.println();

        } else {

            if (round == 1){
                
                //Media file header
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String time = formatter.format(date);

                pw.printf("Media File for IR election on %s%n",time);

                //Initial ballot's and seat allocations:
                pw.print("Candidates: ");
                for (Candidate candidate : candidatesArray){
                    pw.printf("[%s] ", candidate.getName());
                }
                pw.println();
                pw.printf("Total Votes: %d%n%n",totalVotes);

            } else {

                //Every round's update:
                pw.printf("Round %d%n", round);
                pw.print("Eliminated candidates: ");

                for (Candidate candidate: candidatesArray) {
                    if (candidate.isEliminated()) {
                        pw.printf("[%s] ", candidate.getName());
                    }
                }

                pw.println();
                pw.println();

            }

            pw.printf("Round %d totals:%n",round);

            for (Candidate candidate : candidatesArray){
                if (!candidate.isEliminated()){
                    pw.printf("[%s]: %d%n", candidate.getName(),candidate.getNumVotes());
                }
            }

            pw.println();

        }

        pw.close();
        fileWriter.close();

    }

    /**
     * Will ensure that all candidates other than the winner are eliminated
     * @param winner This is the candidate that won and will not be eliminated
     */
    private void eliminateAll(int winner){

        //eliminate all candidates except for the winner
        for (int i = 0; i < totalCandidates; i++){
            if (i != winner){
                candidatesArray[i].eliminate();
            }
        }
    }
    /**
     * Is used to decide a tie between multiple candidates or parties
     * Function is not tested because it is random thus tests are not reliable
     * @param numOfItems is an int that will determine the range of the random number generator
     * @return an int representing the index of the winner
     */
    private int decideTie(int numOfItems){
        random.setSeed(System.nanoTime());
        return random.nextInt(numOfItems);
    }

    /**
     * Getter for candidates array
     * @return Candidates array
     */
    public Candidate[] getCandidates(){
        return candidatesArray;
    }

    /**
     * Getter for the ballots in this election
     * @return ArrayList of the ballots
     */
    public ArrayList<Ballot> getBallots(){
        return ballots;
    }


    /**
     * Getter for the parties in this election
     * @return ArrayList of the parties
     */
    public ArrayList<Party> getParties(){
        return partyArray;
    }
}
