//main.c program to translate the virtual addresses from the virtual.txt
//into the physical frame address. Project 3 - CSci 4061

#include <stdio.h>
#include <stdlib.h>
#include "vmemory.h"

#define INPUT_FILE "../bin/virtual.txt"

int main(int argc, char* argv[])
{
	if (argc > 2) {
		printf("Too many arguments, enter up to one argument\n");
		exit(-1);
	}

	int policy = (argc == 2) ? 1:0;
	initialize_vmanager(policy);

	//TODO: Translate all of the virtual addresses in INPUT_FILE
	//	Print the corresponding physical addresses line by line
	//	Make sure to populate the TLB as you go!
	//	Output the TLB at the start and end along with the hit rate
	// int num = translate_virtual_address(0x82e2e665);
	// int frame = num >> 12;
	// int offset = num & 0xfff;
	// if(num == -1){
	// 	printf("-1\n");
	// }else{
	// 	print_physical_address(frame, offset);
	// }
	populate_tlb(0xfff, 0xfff);
	print_tlb();
	// int i = get_tlb_entry(0x82e2e5);
	// printf("%x\n", i);
	
	//Free the page table
	free_resources();
	return 0;
}
