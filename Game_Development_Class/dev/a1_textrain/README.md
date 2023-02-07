#Name: Varun Reddy

In this project I decided to create a new class called Letter that would store the char, y position, and velocity of each text rain drop. There is an array
called letters that store all the letters that will appear in the program. Each letter that appears is random and the velocity can randomly range from 4-8 pixels 
per frame. There is a 1/10 chance that instead of a random char that is selected there is words from the given text rain string that is selected. This is done
by an array filled with words from the text rain string, then using the random function a random index is selected. The program will parse through the word and 
add each letter of the word to the letters[] in consecutive indexes.