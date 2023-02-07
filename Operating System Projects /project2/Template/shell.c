#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <errno.h>
#include <sys/wait.h>
#include <stdbool.h>
#include <fcntl.h>

#include "util.h"


void cmds(char* cmd, char* arg, int mode) {
	pid_t pid = fork();
	if(pid == -1){
		printf("\nForking failed.\n");
	} else if (pid == 0){
		// File Redirection Replace
		int fdf;
		if (mode == 1) {
			remove(arg);
			fdf = open(arg, O_WRONLY | O_CREAT, 0666);
			if (fdf == -1) {
				perror("Unable to create file.\n");
				close(fdf);
				return;
			}
			dup2(fdf, 1);
		}
		// File Redirection Append
		else if (mode == 2) {
			fdf = open(arg, O_WRONLY | O_APPEND | O_CREAT, 0666);
			if (fdf == -1) {
				perror("Unable to create file.\n");
				close(fdf);
				return;
			}
			dup2(fdf, 1);
		}

		// Pipe
		else if (mode == 3) {
			int fd[2];
            pipe(fd);
            pid_t p = fork();
            if(p >0){
                wait(NULL);
                close(fd[1]);
                if(dup2(fd[0], 0)< 0){
                    close(fd[0]);
                    exit(0);
                }
                cmds(arg, "", 0);
                close(fd[0]);
            }else if(p == 0){
                close(fd[0]);
                if(dup2(fd[1], 1)< 0){
                    close(fd[1]);
                    exit(0);
                }
                cmds(cmd, "", 0);
                close(fd[1]);
            }else{
                perror("Fork fail\n");
				close(fdf);
				exit(0);
            }
			exit(0);
		}

		char *args[100];
		parse_line(cmd, args, " ");
		if (strcmp(args[0], "ls") == 0)  {
			if (execvp("./ls", args) < 0) {
				perror("Command error.\n");
				close(fdf);
				exit(0);
			}
		}
		else if (strcmp(args[0], "wc") == 0)  {
			if (execvp("./wc", args) < 0) {
				perror("Command error.\n");
				close(fdf);
				exit(0);
			}
		}
		else if (strcmp(args[0], "cd") == 0)  {
			if (execvp("./cd", args) < 0) {
				perror("Command error.\n");
				close(fdf);
				exit(0);
			}
		}
		else {
			if (execvp(args[0], args) < 0) {
				perror("Command error.\n");
				close(fdf);
				exit(0);
			}
		}
		close(fdf);
	} else{
		wait(NULL);
	}
}


int main(){
	while(1){
		char emp[4097];
		getcwd(emp, sizeof(emp));
		printf("[4061-shell]%s$ ", emp);
		char str[1000];
		scanf(" %[^\n]s", str);
		char *file_redirection_buf[100];
		char *pipe_buf[100];

		int file_redirection_append_flag = double_char(str, '>');
		int file_redirection = parse_line(str, file_redirection_buf, ">");
		int pipe = parse_line(str, pipe_buf, "|");

		// Exit
		if (strcmp(str, "exit") == 0) {
			exit(0);
		}

		// File Redirection: ">"
		if (file_redirection_append_flag == 0 && file_redirection > 1) {
			cmds(file_redirection_buf[0], trimwhitespace(file_redirection_buf[1]), 1);
		}

		// File Redirection: ">>"
		else if (file_redirection_append_flag == 1) {
			cmds(file_redirection_buf[0], trimwhitespace(file_redirection_buf[1]), 2);
		}

		// Pipe: "|"
		else if (pipe > 1){
			cmds(pipe_buf[0], trimwhitespace(pipe_buf[1]), 3);
		}

		// Vanilla Command
		else {
			cmds(str, "", 0);
		}
	}
	return 0;
}