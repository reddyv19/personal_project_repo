#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include "util.h"
#include <limits.h>
#include <ctype.h>

char *commands[] = {"ls", "cd", "wc", "exit"};

int parse_line(char *input, char *tokens[], char *delim) {
    int i = 0;
    char *tok = strtok(input, delim);
    while(tok != NULL) {
        tokens[i] = tok;
        i++;
        tok = strtok(NULL, delim);
    }
    tokens[i] = NULL;
    return i;
}

int double_char(char input[], char delim) {
    char previous_char = 'n';
    for (int i = 0; i < strlen(input) - 1; i++) {
        if ((input[i] == delim) && (input[i+1] == delim)) {
            return 1;
        }
        previous_char = input[i];
	}
    return 0;
}

enum command_type get_command_type(char *command) {
    int i = 0;

    for (i=0; i<ERROR; i++) {
        if (strcmp(command, commands[i]) == 0) {
            return i;
        }
    }

	return ERROR;
}

char *trimwhitespace(char *str)
{
  char *end;

  // Trim leading space
  while(isspace((unsigned char)*str)) str++;

  if(*str == 0)
    return str;

  // Trim trailing space
  end = str + strlen(str) - 1;
  while(end > str && isspace((unsigned char)*end)) end--;

  // Write new null terminator character
  end[1] = '\0';

  return str;
}
