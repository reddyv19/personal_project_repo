#include<stdlib.h>
#include<stdio.h>
#include <unistd.h>
#include<string.h>

void wc(int mode, char* path){
	/*Feel free to change the templates as needed*/
	/*insert code here*/
	ssize_t read;
	size_t len = 0;
	char *line = NULL;

	 FILE *file = fopen(path, "r");
        if (file == NULL) {
            perror("Failed to open file.\n");
        }
	
	int line_count = -1;
	int word_count = 1;
	int char_count = 0;

	while((read = getline(&line, &len, file)) != -1) {
		line_count++;
		char prev;
		for (int i = 0; i < strlen(line); i++) {
			char_count++;
			// printf("%c %s", line[i], "\n");
			if ((line[i] == ' '||line[i] == '\n'||line[i] == '\t'||line[i] == '\0')&&(prev!=' '&&prev!='\n'&&prev!='\t'&&prev!='\0')) {
				if (strlen(line) > 1) {
					word_count++;
				}
			}
			prev = line[i];
		}
	}

	if (mode == 1) {
		printf("%i %s", line_count, "\n");
	}
	else if (mode == 2)
	{
		printf("%i %s", word_count, "\n");
	}
	else if (mode == 3)
	{
		printf("%i %s", char_count, "\n");
	}
	else {
		printf("%i %s %i %s %i %s", line_count, "\t", word_count, "\t", char_count, "\n");
	}
}

int main(int argc, char** argv){
	if(argc>2){
		if(strcmp(argv[1], "-l") == 0) { 
			wc(1, argv[2]);
		} else if(strcmp(argv[1], "-w") == 0) { 
			wc(2, argv[2]);
		} else if(strcmp(argv[1], "-c") == 0) { 
			wc(3, argv[2]);
		} else {
			printf("Invalid arguments\n");
		}
	} else if (argc==2){
	 	if(strcmp(argv[1], "-l") == 0) { 
			wc(1, NULL);
		} else if(strcmp(argv[1], "-w") == 0) { 
			wc(2, NULL);
		} else if(strcmp(argv[1], "-c") == 0) { 
			wc(3, NULL);
		} else {
    		wc(0, argv[1]);
    	}
  	} else {
  		wc(0,NULL);
  	}

	return 0;
}