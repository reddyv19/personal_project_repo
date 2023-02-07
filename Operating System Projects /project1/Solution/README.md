test machine: CSELAB_ATLAS
* date: 10/07/21
* name: Carson Miner, Varun Reddy, Ethan Phou
* x500: miner188, reddy215, phou0016

Purpose of program: Given a .txt file which contains multiple lines of command line commands and edges of a directed graph. The program
executes the commands of each node in the graph using Depth First Order with the capability of running multiple processes in parrallel if given 
-p as an argument.

Compilation: Change directory in terminal to the Solution directory. Run make clean; make in terminal. 
Run ./depGraph <Optional: -p flag for parallel execution> <filename.txt>.

Execution: Each command given in the filename.txt file will be executed in the order of DFS given the directed graph edges. The way the .txt file
is processed is in the createDepGraph() function in "graph.h". Each node represents a command and is linked to another node using the edges given in the
.txt file. The program then creates multiple processes using fork() and goes through the graph in DFS order. The commands that the node represent
are executed using exec(). The results are written into results.txt and displayed through terminal.

Assumption: User can compile C code. 

Contributions: This project was mainly done doing peer programming most of was done together over a Discord call, Carson wrote the error handling and file export parts. 


