
all: main

main: lab8.o orig_funcs.o
	gcc orig_funcs.o lab8.o -o main

lab8.o: lab8.c
	gcc -c lab8.c

.PHONY: clean

clean: 
	rm main lab8.o
