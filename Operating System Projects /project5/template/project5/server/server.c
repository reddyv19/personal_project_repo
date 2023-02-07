#include "server.h"

#define NUM_ARGS 1

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <pthread.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>
#include <stdint.h>
#include <sys/msg.h>

#define MSGSZ 12800

struct msgbuf {
   long mtype;
   char mtext[MSGSZ];
};


int wordCount[26] = {0};


//start of the threads
void* process_file(void *param)
{   
    int error;
	printf("here\n");
    struct msgbuf buf = {((int) param), ""};
	printf("here2\n");
    key_t key = 100;
    int mid;
    // Get the message queue id
    mid = msgget(key, 0666 | IPC_CREAT);
    // A server typically runs infinitely, with some boolean flag to terminate.
	while (1) {
        printf("Waiting to recieve from: \n");
		if(msgrcv (mid, (void *)&buf, sizeof(buf), ((int) param + 20), 0) < 0){
			printf("Failed to recieve from client.\n");
		}else{
            if(strcmp(buf.mtext, "END")){
                break;
            } else
			printf("Thread: %i recieved File path: %s from\n", ((int) param), buf.mtext);

            //Read the file on a line by line basis
            size_t read;
            size_t len = 0;
            char *line = NULL;

            // Opens file in read mode.
            FILE *file = fopen(buf.mtext, "r");
            // Checks if call fails
            if (file == NULL) {
                perror("Failed to open file.\n");
            }
            while((read = getline(&line, &len, file)) != -1) {
                int firstLetter = line[0]-65;
                if(firstLetter < 27 || firstLetter > -1)
                    wordCount[firstLetter] += 1;
                else
                    perror("Line doesn't start with an alphabet symbol.\n");
            }
            fclose(file);

            // Send acknowledgement to the client
            printf("Acknowledged!\n File path: %s\nThread: %i\n", buf.mtext, *((int *) param));
            struct msgbuf ackbuf = {((int) param) + 20, "ACK"};
		    msgsnd(mid, (void *)&ackbuf, sizeof(ackbuf), 0);
        }
	}
}

int main(int argc, char *argv[]){
    int error;

    if (argc != NUM_ARGS + 1) {
		printf("Wrong number of args, expected %d, given %d\n", NUM_ARGS, argc - 1);
		exit(1);
	}

     key_t key = 100;
     int mid;
     // Get the message queue id
    mid = msgget(key, 0666 | IPC_CREAT);
    
    printf("Server has been created.\n");

    int num_clients = atoi(argv[1]);

    // Creates i threads specified by the commandline input
    pthread_t tid[num_clients];
	for(int i = 0; i < num_clients; i++){
		error = pthread_create(&tid[i], NULL, process_file, (void *) i);
		if(error !=0){
			perror("Failed to create thread.\n");
		}
	}
	for(int i = 0; i < num_clients; i++){
		pthread_join(tid[i], NULL);
	}

    

    for(int i = 0; i < num_clients; i++){
        struct msgbuf buf = {i + 20, ""};
        strcat(buf.mtext, ""+wordCount[0]);
        for(int i = 1; i < 27; i++){
            strcat(buf.mtext, "#");
            strcat(buf.mtext, ""+wordCount[0]);
        }
        msgsnd(mid, (void *)&buf, sizeof(buf), 0);
        printf("Sending final letter count to client process: %i", i);
    }
    printf("Ending the server.\n");
}