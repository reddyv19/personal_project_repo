/*test machine: CSELAB_ATLAS
* date: 10/07/21
* name: Carson Miner, Varun Reddy, Ethan Phou
* x500: miner188, reddy215, phou0016
*/

#include "graph.h"
#define OUTPUT_FILE_NAME "results.txt"

struct AdjListNode* newAdjListNode(int dest) { 
    struct AdjListNode* newNode = (struct AdjListNode*) malloc(sizeof(struct AdjListNode)); 
    newNode->dest = dest; 
    newNode->next = NULL; 
    return newNode; 
} 

struct DepGraph* createDepGraph(FILE *input, char cmds[][550]) {
	ssize_t read;
	size_t len = 0;
	char *line = NULL;
    int V;

    // number of nodes
    read = getline(&line, &len, input);
    sscanf(line, "%d", &V);

    // skip the blank line
    read = getline(&line, &len, input);

    int cmdIter = 0;
	while((read = getline(&line, &len, input)) != -1) {
		if(strcmp(line, "\n") == 0){
			break;
		}
		// read commands
		strcpy(cmds[cmdIter++], line);
	}

    // Graph creation
    struct DepGraph* graph =  (struct DepGraph*) malloc(sizeof(struct DepGraph)); 
    graph -> V = V;  
    graph -> array =  (struct AdjList*) malloc(V * sizeof(struct AdjList)); 

    int i; 
    for (i = 0; i < V; ++i) {
        graph->array[i].head = NULL;
        graph->array[i].visit = 0;
        // TODO: you can add corresponding cmd to each node, it depends on you
        // if you want to save them to graph, feel free to change graph.h and anything needed
        // otherwise you don't need to do anything here but you will extract
        // corresponding command from cmds in DFSVisit
        

    }

    while((read = getline(&line, &len, input)) != -1) {
        // TODO: extract src and dest from current line
        // you may find strtok very helpful
        const char s[2] = " ";
        char *token;
        int src, dest;

        token = strtok(line,s);
        src = atoi(token);
        token = strtok(NULL,s);
        dest = atoi(token);
    
        // TODO: add current edge to graph (src <-> dest forms an edge)
        addEdge(graph, src, dest);
	}

    return graph; 
}


void addEdge(struct DepGraph* graph, int src, int dest) {
    struct AdjListNode* temp = newAdjListNode(dest);
    if(graph->array[src].head==NULL){
        graph->array[src].head = temp;
    }else{
        struct AdjListNode* ptr = graph->array[src].head;
            while(ptr->next != NULL){
                ptr = ptr->next;
            }
        ptr->next = temp;
    }
}

void DFSVisit(struct DepGraph* graph, int node, char cmds[][550], int mode) {
    // start from node, iterate over its adjListNode
    struct AdjListNode* adjListNode = graph -> array[node].head;
    // If the user did not specify the parallel execution flag
    if(mode == 0){
        while(adjListNode != NULL) {
            pid_t pid = fork();
            if (pid == 0) {
                DFSVisit(graph, adjListNode->dest, cmds, mode);
            }
            else {
                wait(NULL);
            }
            adjListNode = adjListNode->next;
        }
        char* ptr = strtok(cmds[node], "\n");
        ptr = strtok(ptr, " ");
        char* words[11];
        int counter = 0;
        while(ptr!=NULL) {
            words[counter] = ptr;
            ptr = strtok(NULL, " ");
            counter++;
        }
        words[counter] = NULL;

        FILE *output_file = fopen(OUTPUT_FILE_NAME, "a");
        if (output_file == NULL) {
            perror("Failed to open file.\n");
        }
        else {
            // Prints pids and args to file
            fprintf(output_file, "%i %i %s", getpid(), getppid(), cmds[node]);

            // Print args to cmds to file
            for (int i = 1; i < counter; i++) {
                fprintf(output_file, " %s",  words[i]);
            }
            fprintf(output_file, "%s", "\n");
        }
        fclose(output_file);

        // Attempts to execute cmds
        if (execvp(words[0], words) < 0) {
            perror("Command failed to execute.\n");
        }
    } 
    // If the user specified the parallel execution flag
    else if(mode == 1) {
        while(adjListNode != NULL) {
            pid_t pid = fork();
            if (pid == 0) {
                DFSVisit(graph, adjListNode->dest, cmds, mode);
            }
            
            adjListNode = adjListNode->next;
        }
        while(wait(NULL)>0){
        }
        char* ptr = strtok(cmds[node], "\n");
        ptr = strtok(ptr, " ");
        char* words[11];
        int counter = 0;
        while(ptr!=NULL){
            words[counter] = ptr;
            ptr = strtok(NULL, " ");
            counter++;
        }
        words[counter] = NULL;

        FILE *output_file = fopen(OUTPUT_FILE_NAME, "a");
        if (output_file == NULL) {
            perror("Failed to open file.\n");
        }
        else {
            // Prints pids and args to file
            fprintf(output_file, "%i %i %s", getpid(), getppid(), cmds[node]);

            // Print args to cmds to file
            for (int i = 1; i < counter; i++) {
                fprintf(output_file, " %s",  words[i]);
            }
            fprintf(output_file, "%s", "\n");
        }
        fclose(output_file);

        // Attempts to execute cmds
        if (execvp(words[0], words) < 0) {
            perror("Command failed to execute.\n");
        }
    }
    exit(0);
}

void processGraph(struct DepGraph* graph, char cmds[][550], int mode) {
    int i;
    remove(OUTPUT_FILE_NAME);
    int child = fork();
    if (child == 0) {
	   DFSVisit(graph, 0, cmds, mode);
    }
    else {
        wait(NULL);
    }
}
