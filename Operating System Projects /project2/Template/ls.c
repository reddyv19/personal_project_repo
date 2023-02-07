#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <dirent.h>
#include <string.h>
#include <stdbool.h>

void ls(char *path, bool recurse_flag) {
	DIR* dir;
	if(path == NULL){
		dir = opendir(".");
	}else{
		dir = opendir(path);
	}

	if(dir == NULL){
		printf("Could not open directory\n");
		return;
	}
	
	struct dirent* entry;
	char emp[4097];
	getcwd(emp, sizeof(emp));
	printf("In directory: %s\n", emp);
	while ((entry = readdir(dir)) != NULL ) {

		// TODO: continue if d_name is . or ..
		if(strcmp(entry->d_name, ".")==0||strcmp(entry->d_name, "..")==0){
			continue;
		}
		printf("%s\n", entry->d_name);
		if(entry->d_type == DT_DIR&&(recurse_flag==true)){
			chdir(entry->d_name);
			ls(NULL, true);
			chdir("..");
			printf("In directory: %s\n", emp);
		}
		
	}
}

int main(int argc, char *argv[]){
	if(argc < 2){ // No -R flag and no path name
		ls(NULL, false);
	} else if(strcmp(argv[1], "-R") == 0) { 
		if(argc == 2) { // only -R flag
			ls(NULL, true);
		} else { // -R flag with some path name
			ls(argv[2], true);
		}
	}else { // no -R flag but path name is given
    	ls(argv[1], false);
  }
	return 0;
}
