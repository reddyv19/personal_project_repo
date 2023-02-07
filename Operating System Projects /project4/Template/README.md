test machine: CSELAB_ATLAS
* date: 11/23/21
* name: Carson Miner, Varun Reddy, Ethan Phou
* x500: miner188, reddy215, phou0016

Purpose of program: The purpose of this program is to use mutex locks to synchronize operations on a concurrent linked list.

Compilation: Change directory in terminal to the "Template" directory. Run "make clean; make" in terminal.

Run: To run the program execute "./tlist FILENAME THREAD_COUNT" in the terminal.

Assumption: User can compile C code. 

Contributions: This project was mainly done doing peer programming most of was done together over a Discord call.

Test Cases: We used "./tlist foo.txt THREAD_COUNT", with foo.txt containing six single words seperated by new lines, and various values 1-16 for THREAD_COUNT