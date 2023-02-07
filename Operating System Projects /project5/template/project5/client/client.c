#include "client.h"

#include <unistd.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <errno.h>
#include <fcntl.h>
#include <dirent.h>
#include <stdbool.h>
#include <pthread.h>

#define MSGSZ 128

struct msgbuf {
   long mtype;
   char mtext[MSGSZ];
};

struct send_msg_struct {
    int files;
    int num_clients;
    int iteration;
};

int arr_index = 0;

int countTxt(char *path, int file_count) {
	DIR* dir;
	if(path == NULL){
		dir = opendir(".");
	}else{
		dir = opendir(path);
	}

	if(dir == NULL){
		perror("Could not open directory\n");
		return -1;
	}
	
	struct dirent* entry;
	char emp[4097];
	getcwd(emp, sizeof(emp));
	//printf("In directory: %s\n", emp);
	while ((entry = readdir(dir)) != NULL ) {
		if(strcmp(entry->d_name, ".")==0||strcmp(entry->d_name, "..")==0){
			continue;
		}

		//printf("%s\n", entry->d_name);
        if (entry->d_type == DT_REG) {
            const char s[2] = ".";
            char *token = strtok(entry->d_name, s);
            token = strtok(NULL, s);

            if (token != NULL && strcmp(token, "txt") == 0){
                file_count++;
            }
        }
		else if (entry->d_type == DT_DIR) {
            char path_addr[100];
            strcpy(path_addr, path);

            strcat(path_addr, "/");
            strcat(path_addr, entry->d_name);

            //printf("Path: %s", path_addr);
            file_count = countTxt(path_addr, file_count);
		}
	}
    return file_count;
}

char** arrayCreator(char *path, int size, int array_index, char**array) {
    
	DIR* dir;
	if(path == NULL){
		dir = opendir(".");
	}else{
		dir = opendir(path);
	}

	if(dir == NULL){
		printf("Could not open directory\n");
		return -1;
	}
	
	struct dirent* entry;
	char emp[4097];
	getcwd(emp, sizeof(emp));
	//printf("In directory: %s\n", emp);
	while ((entry = readdir(dir)) != NULL ) {
		if(strcmp(entry->d_name, ".")==0||strcmp(entry->d_name, "..")==0){
			continue;
		}

        char path_addr[100];
        strcpy(path_addr, path);

        strcat(path_addr, "/");
        strcat(path_addr, entry->d_name);
        if (entry->d_type == DT_REG) {
            const char s[2] = ".";
            char *token = strtok(entry->d_name, s);
            token = strtok(NULL, s);

            size_t len = strlen(path_addr);
            char * path_str = (char*) malloc( len + 1 );
            strcpy(path_str, path_addr);
            if (token != NULL && strcmp(token, "txt") == 0){
                array[arr_index] = path_str;
                //: %s To: %i\n", array[arr_index], arr_index);
                arr_index++;
            }
        }
		else if (entry->d_type == DT_DIR) {
            arrayCreator(path_addr, size, arr_index, array);
		}
	}
    return array;
}

void txtRelay(char** array, int file_count, int num_clients) {
    int paths_per_file = file_count / num_clients;
    //printf("%i", paths_per_file);
    
    for (int i = 0; i < num_clients; i++) {
        char file_path[] = "./ClientInput/Client";
        char file_str[100];
        char index_str[4];
        sprintf(index_str, "%i", i);
        strcpy(file_str, file_path);
        strcat(file_str, index_str);
        strcat(file_str, ".txt");

        //printf("%s \n", file_str);
        FILE *file = fopen(file_str, "w");
        if (file == NULL) {
            perror("Failed to open file.\n");
        }

        for(int j = paths_per_file * i; j < paths_per_file + (paths_per_file * i); j++){
            fprintf(file, "%s \n", array[j]);
        }
        fclose(file);

    }
    return;
}


void *send_msgs(void *args) {
    struct send_msg_struct *arguments = args;
    int files = arguments->files;
    int num_clients = arguments->num_clients;
    int iteration = arguments->iteration;

    int paths_per_file = files / num_clients;
    char file_path[] = "./ClientInput/Client";
    char file_str[100];
    char index_str[4];
    sprintf(index_str, "%i", iteration);
    strcpy(file_str, file_path);
    strcat(file_str, index_str);
    strcat(file_str, ".txt");

    ssize_t read;
    size_t len = 0;
    char line[256];

    
    FILE *read_file = fopen(file_str, "r");
    if (read_file == NULL) {
        perror("Failed to open file.\n");
    }
    //printf("HERE %s \n", file_str);

    while(fgets(line, sizeof(line), read_file)) {
        struct msgbuf buf;

        key_t key = 100;
        int mid;
        // TODO: get message queue id
        mid = msgget(key, 0666 | IPC_CREAT);

        // set type to 10
        buf.mtype = iteration + 20;
        char test[MSGSZ] = "test";
        //buf.mtext = test;
        strcpy(buf.mtext, line);
        printf("Sending... %s \n", line);
        msgsnd (mid, (void *)&buf, sizeof (buf), 0);
        
        if(msgrcv (mid, (void *)&buf, sizeof(buf), iteration + 20, 0) < 0){
			printf("Failed to recieve from client.\n");
		}else{
            if(strcmp(buf.mtext, "ACK"))
                printf("Acknowledgement from the server recieved.\n");
        }
        // destory message queue
        msgctl(mid, IPC_RMID, NULL);

        //printf("Line: %s", line);
    }
    fclose(read_file);
}

int main(int argc, char *argv[]){
    if(argc<3){
        printf("Not enough arguments\n");
        return 0;
    }
    int num_clients = atoi(argv[2]);
    char *path = argv[1];
    DIR* dir = opendir("ClientInput");
    if (!dir) {
        if (mkdir("ClientInput", 777) == -1) {
            perror("Failed to create.\n");
            return -1;
        }
    }
    int count = 0;
    int files = countTxt(path, count);
    int error;

    //printf("\nTotal number of txt files: %i\n", files);
    char ** array = malloc(files * sizeof(char*));
    for (int i = 0; i < files; i++) {
        array[i] = malloc(20 * sizeof(char));
    }
    array = arrayCreator(path, files, count, array);
    for (int i = 0; i < files; i++) {
        //printf("Here: %s at index: %i \n", array[i], i);
    }

    txtRelay(array, files, num_clients);

    pthread_t tid[num_clients];
	for(int i = 0; i < num_clients; i++) {
        struct send_msg_struct args;
        args.files = files;
        args.num_clients = num_clients;
        args.iteration = i;
        error = pthread_create(&tid[i], NULL, send_msgs, &args);
		if(error != 0){
			perror("Failed to create thread.\n");
		}
    }
    
    for(int i = 0; i < num_clients; i++){
            pthread_join(tid[i], NULL);
        }
    free(array);
}