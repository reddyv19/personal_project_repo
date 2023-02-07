#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pthread.h"
#include "linked_list.h"

// sequence counter
int seq_ctr = 0;

//Read the file on a line by line basis
char* read_line(char* fname, int line_no) 
{
    size_t read;
    size_t len = 0;
    int line_count = 0;
    char *line = NULL;

    // Opens file in read mode.
    FILE *file = fopen(fname, "r");
    // Checks if call fails
    if (file == NULL) {
        perror("Failed to open file.\n");
    }
    while((read = getline(&line, &len, file)) != -1) {
        // If line number is equal to line requested.
        if (line_count == line_no) {
            fclose(file);
            return line;
        }
        line_count++;
    }
    fclose(file);
    return EOF;
} 

//traverse the linked list
void traversal(node *head)
{
	// TODO
	node *ptr = head;
    while(ptr != NULL){
        int num = ptr->seq_no;
        int line = ptr->line_no;
        char *str = ptr->content;
        printf("%d, %d, %s", num, line, str);
		node *oldPtr = ptr;
        ptr = ptr -> next;
	}
	printf("\n");
}

// insert the node into the linked list
void insert(node **phead, node *newnode)
{
	// TODO
	 // TODO
    node *iterator;
    // Checking to see if the head is null and sets it equal to the new node if it is
    if(*phead == NULL)
        *phead = newnode;
    else{
        iterator = *phead;
        // Checking to see if the head is greater than the newnode
        if(newnode->line_no < iterator->line_no){
            newnode->next = iterator;
            *phead = newnode;
            return;
        }
        while(iterator -> next != NULL){
            // Checks to see if the current node's line number is less than the newnode's line number and if the next node is greater than
            // the newnode's line number
            if(iterator->line_no < newnode->line_no && iterator->next->line_no>newnode->line_no){
                newnode->next = iterator->next;
                iterator->next = newnode;
                return;
            }else{
                iterator = iterator -> next;
            }
        }
        // Adding the new node to the end of the linked list
        iterator -> next = newnode;
    }
}

//create a new node structure
node* create_node(int line_no, char *line)
{
	// TODO
	node *tempnode = malloc(sizeof(node));
	tempnode -> seq_no = seq_ctr;
	tempnode -> line_no = line_no;
	tempnode -> content = line;
	tempnode -> next = NULL;
	seq_ctr++;
	return tempnode;
}
