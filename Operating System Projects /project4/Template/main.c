#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "linked_list.h"

//global line-number counter to be sync'ed.
int line_ctr = -1;


//initialization of mutex locks
pthread_mutex_t line_lock = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t list_lock = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t seq_lock = PTHREAD_MUTEX_INITIALIZER;
//at least two mutex locks should be used

//start of the threads
void* process_file(void *param)
{
	
	 char *file_in = (char *)param;
	 //use lineNum to keep track line_ctr at the begnning so that line_ctr is not
	 //incremented erractically
	 int lineNum = 0;
    while(1){
		//used this mutex lock to keep track of old lineNum and icnrement line_ctr
		pthread_mutex_lock(&line_lock);
		lineNum = line_ctr;
		line_ctr++;
		pthread_mutex_unlock(&line_lock);
		//checks if the program is at the end of the file
		char *the_Line = read_line(file_in, lineNum);
		if(the_Line==EOF){
			break;
		}
		//used this mutex lock to insert node
		pthread_mutex_lock(&list_lock);
		insert(&head, create_node(lineNum, the_Line));
		pthread_mutex_unlock(&list_lock);
    }
}

int main(int argc, char* argv[])
{
	FILE* in_ptr;
	char *filename = NULL;
	int threads = 0;
	if (argc != 3) {
		printf("Incorrect arg count. Enter file name and thread count\n");
		exit(EXIT_FAILURE);
	}

	threads = atoi(argv[2]);
	filename = argv[1];
	if (threads < 1 || threads > 16) {
		printf("Incorrect number of threads. Enter between 1 and 16\n");
		exit(EXIT_FAILURE);
	}

	// TODO: create threads and join them
	int error;
	pthread_t tid[threads];
	for(int i = 0; i < threads; i++){
		error = pthread_create(&tid[i], NULL, process_file, (void *) filename);
		if(error !=0){
			perror("Failed to create thread.\n");
		}
	}
	for(int i = 0; i < threads; i++){
		pthread_join(tid[i], NULL);
	}

	traversal(head);
	
	return 0;
}
