//Implement the API modeling the translation of virtual page address to a
//physical frame address. We assume a 32 bit virtual memory and physical memory.
//Access to the page table is only via the CR3 register.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "vmemory.h"

#define OUT_TLB "../bin/tlb_out.txt"


bool FIFO_policy = true;
int **cr3;
int tlb[8][2];
int top = 0;
float hit = 0.0;
float amount_translated = 0.0;


//
// You can declare global variables necessary for your TLB implementation here
//

//
// More static helper functions can be implemented here
//


// The implementation of get_vpage_cr3 is provided in 
// an object file, so no need to re-implement it
void initialize_vmanager(int policy) 
{
	// Set LFU policy when passed as a parameter
	if (policy)
		FIFO_policy = false;
	// Set base pointer to page table
	cr3 = get_vpage_cr3();
	//printf("cr3: %p\n", cr3);
	
	// You can add other initialization here as necessary
	
	for(int i = 0; i < 8; i++){
		for(int j = 0; j < 2; j++){
			tlb[i][j] = -1;
		}
	}
}

//
// The implementation of the following functions is required
//
int translate_virtual_address(unsigned int v_addr)
{
	//TODO
	amount_translated++;
	int physical_addr;
	int fb = v_addr >> 22;
	int sb = v_addr >>12;
	sb = sb & (0x3ff);
	int offst = v_addr & 0xfff;
	int *p1 = *(cr3+fb);
	if(p1==NULL){
		return -1;
	}
	physical_addr = *(p1+sb);
	if(physical_addr==(-1)){
		return -1;
	}
	physical_addr = physical_addr << 12;
	physical_addr = physical_addr | offst;
	return physical_addr;
}

void print_physical_address(int frame, int offset)
{
	int newframe = frame << 12;
	newframe = newframe | offset;
	printf("0x%x\n", newframe);
	return;
}

int get_tlb_entry(int n)
{
	int tlb_entry;
	amount_translated++;
	for(int i = 0; i < 8; i++){
		if (tlb[i][0] == n) {
			hit++;
			return tlb[i][1];
		}
	}
	return -1;
}

void populate_tlb(int v_addr, int p_addr) 
{
	//TODO
	amount_translated++;
	tlb[top][0] = v_addr;
	tlb[top][1] = p_addr;
	top++;
	if(top==8){
		top = 0;
	}
	return;
}

float get_hit_ratio()
{
	//TODO
	return (hit/amount_translated);
}

// Write to the file denoted by OUT_TLB
void print_tlb()
{
	//TODO

	FILE *file = fopen("tlb_out.txt", "a");
        if (file == NULL) {
            perror("Failed to open file.\n");
        }

	for(int i = 0; i < 8; i++){
			if(tlb[i][0] != -1)
				fprintf(file, "Index: %d  Virtual: 0x%05x  Physical: 0x%05x\n", i, tlb[i][0], tlb[i][1]);
	}
	fprintf(file, "\n");
	return;
}
