/*
 * CSci-4611 Assignment #1 Text Rain
 */


/* Note: if Processing's video library does not support your particular combination of webcam and
   operating system, then the Sketch may hang in the setup() routine when the list of available
   image capture devices is requestd with "Capture.list()".  If this happens, you can skip all of
   the camera initilization code and just run in movie mode by setting the following global 
   variable to true. 
   
   If you having trouble accessing the cameara on a mac laptop due to a security issue.  Some
   students found this post helpful: 
   https://github.com/processing/processing-video/issues/134#issuecomment-664778394
 */
boolean forceMovieMode = false;

// Global vars used to access video frames from either a live camera or a prerecorded movie file
import processing.video.*;

String[] cameraModes;
Capture cameraDevice;
Movie inputMovie;
boolean initialized = false;


// Both modes of input (live camera and movie) will update this same variable with the lastest
// pixel data each frame.  Use this variable to access the new pixel data each frame!
PImage inputImage;
//redVal and greenVal are used to adjust the color of the text
int redVal = 0;
int greenVal = 0;
//String rainStr is the rain text that is used
String rainStr = "Rain on Earth is pretty awesome, it is essential for all life not in the oceans to exist. It places a pivotal role in almost all atmospheric cycles that are necessary for most ecosystems on this planet. It nice that I live with Earth rain and not Venus rain which is made of sulfuric acid.";
//String rainStrNoSpace is used when a random character is selected to be a letter in the text rain
String rainStrNoSpaces = rainStr.replace(" ","");
//String[] rainStrWords is used when a random word is selected to appear in the text rain together
String[] rainStrWords = rainStr.split(" ");
//Letter[] letter is used to store all the information about each letter that appears in the text rain
Letter[] letters = new Letter[128];

//threshold is used to determine whether the text will fall or rise in the program
int threshold = 128;
//debugMode sets the video to black and white when the spacebar is pressed
boolean debugMode = false;



// Called automatically by Processing, once when the program starts up
void setup() {
  size(1280, 720);  
  inputImage = createImage(width, height, RGB);
  
  if (!forceMovieMode) {
    println("Querying avaialble camera modes.");
    cameraModes = Capture.list();
    println("Found " + cameraModes.length + " camera modes.");
    for (int i=0; i<cameraModes.length; i++) {
      println(" " + i + ". " + cameraModes[i]); 
    }
    // if no cameras were detected, then run in offline mode
    if (cameraModes.length == 0) {
      println("Starting movie mode automatically since no cameras were detected.");
      initializeMovieMode(); 
    }
    else {
      println("Press a number key in the Processing window to select the desired camera mode.");
    }
  }
  textFont(loadFont("Copperplate-Light-16.vlw"));
  
  //code below is used to decide and create all the letters that are going to become the text rain
  for(int i = 0; i < 128; i++){
    //isWord is used to determine wether a whole word or just a letter is added to the letters[]
    int isWord = (int) random(10);
    //if isWord == 5 then characters are going to go back to back spell out a word meaning there is a 1/10 chance of there being a random word rather
    //than a random char
    if(isWord == 5){
      int randWord = (int)random(rainStrWords.length);
      String currWord = rainStrWords[randWord];
      //using a for loop to add each letter in the word to the letters[]
      for(int j = 0; j<currWord.length(); j++){
        if(i+j < 128){
          letters[i+j] = new Letter(currWord.charAt(j));
          //velocity adds random variation to the rain
          letters[i+j].velocity = (int) random(4)+ 4;
        }
      }
      //skips the length of the word so that there wont be an out of bounds error trying to letters[]
      i = i + currWord.length()-1;
    }else{
      //this code is run if isWord does not equal 5
      int randChar = (int)random(rainStrNoSpaces.length());
      char currChar = rainStrNoSpaces.charAt(randChar);
      letters[i] = new Letter(currChar);
      letters[i].velocity = (int) random(4)+ 4;
    }
      
  }
}



// Called automatically by Processing, once per frame
void draw() {
  // start each frame by clearing the screen
  background(0);
    
  if (!initialized) {
    // IF NOT INITIALIZED, DRAW THE INPUT SELECTION MENU
    drawMenuScreen();      
  }
  else {
    // IF WE REACH THIS POINT, WE'RE PAST THE MENU AND THE INPUT MODE HAS BEEN INITIALIZED


    // GET THE NEXT FRAME OF INPUT DATA FROM LIVE CAMERA OR MOVIE  
    if ((cameraDevice != null) && (cameraDevice.available())) {
      // Get image data from cameara and copy it over to the inputImage variable
      cameraDevice.read();
      inputImage.copy(cameraDevice, 0,0,cameraDevice.width,cameraDevice.height, 0,0,inputImage.width,inputImage.height);
    }
    else if ((inputMovie != null) && (inputMovie.available())) {
      // Get image data from the movie file and copy it over to the inputImage variable
      inputMovie.read();
      inputImage.copy(inputMovie, 0,0,inputMovie.width,inputMovie.height, 0,0,inputImage.width,inputImage.height);
    }


    // DRAW THE INPUTIMAGE ACROSS THE ENTIRE SCREEN
    // Note, this is like clearing the screen with an image.  It will cover up anything drawn before this point.
    // So, draw your text rain after this!
    set(0, 0, inputImage);
    

    // DRAW THE TEXT RAIN, ETC.
    // TODO: Much of your implementation code should go here.  At this point, the latest pixel data from the
    // live camera or movie file will have been copied over to the inputImage variable.  So, if you access
    // the pixel data from the inputImage variable, your code should always work, no matter which mode you run in.
    loadPixels(); 
    //if statement below checks if the program is in debug mode, if in debug mode the video is changed to black and white
    //otherwise the video is change to grayscale
    if(debugMode){
      for(int i = 0; i<pixels.length; i++){
        if((red(pixels[i])+green(pixels[i])+blue(pixels[i]))/3 < threshold){
           pixels[i] = color(0,0,0);
        }else{
           pixels[i] = color(255,255,255);
        }
      }
    }else{
      for(int i = 0; i<pixels.length; i++){
        int average = (int) (red(pixels[i])+green(pixels[i])+blue(pixels[i]))/3;
        pixels[i] = color(average, average, average);
      }
    }
    updatePixels();
    
    //segment of code below flips the video so that there is a mirror effect
    color[] newPixels = pixels.clone();
    for (int i = 0; i < pixels.length; i++) {
    pixels[i] = newPixels[i - 2 * (i % 1280) + 1280 - 1];
    }
    updatePixels();
    
    //for loop below adjusts each letter's y value
    for(int i = 0; i < 128; i++){
      text(letters[i].letter, i*12, letters[i].ypos);
      color currColor = pixels[letters[i].ypos*1280+i*12];
      //if the grayscale value is above a certain amount the letter moves down
      if((red(currColor)+green(currColor)+blue(currColor))/3>=threshold){
        if(letters[i].velocity+letters[i].ypos<720){
        letters[i].ypos+=letters[i].velocity;
        }
      //if the grayscale value is below a certain amount the letter moves up
      }else{
        if(letters[i].ypos-4>=0){
        letters[i].ypos-=4;
        }
      }
      //if a letter hits 710, the letter resets back to the top
      if(letters[i].ypos>=710){
        letters[i].ypos = 10;
      }
    }
    
    //code below changes the color of the letters
    fill(redVal, greenVal, 255);
    if(redVal>=255&&greenVal==0){
      greenVal++;
      redVal = 0;
    }else if(greenVal>=255&&redVal == 0){
      redVal++;
      greenVal = 0;
    }else if (greenVal <255&&redVal == 0){
      greenVal++;
    }else{
      redVal++;
    }
    

  }
}


// Called automatically by Processing once per frame

void keyPressed() {
  if (!initialized) {
    // CHECK FOR A NUMBER KEY PRESS ON THE MENU SCREEN    
    if ((key >= '0') && (key <= '9')) { 
      int input = key - '0';
      if (input == 0) {
        initializeMovieMode();
      }
      else if ((input >= 1) && (input <= 9)) {
        initializeLiveCameraMode(input);
      }
    }
  }
  else {
    // CHECK FOR KEYPRESSES DURING NORMAL OPERATION
    // TODO: Fill in your code to handle keypresses here..
    if (key == CODED) {
      if (keyCode == UP) {
        if(threshold<253){
        threshold = threshold+2;
        }
      }
      else if (keyCode == DOWN) {
        // down arrow key pressed
        if(threshold>1){
        threshold = threshold-2;
        }
      }
    }
    else if (key == ' ') {
      // spacebar pressed
      if(debugMode){
        debugMode = false;
      }else{
        debugMode = true;
        System.out.println("You are in debug mode");
      } 
    } 
  }
}



// Loads a movie from a file to simulate camera input.
void initializeMovieMode() {
  String movieFile = "TextRainInput.mov";
  println("Simulating camera input using movie file: " + movieFile);
  inputMovie = new Movie(this, movieFile);
  inputMovie.loop();
  initialized = true;
}


// Starts up a webcam to use for input.
void initializeLiveCameraMode(int cameraMode) {
  println("Activating camera mode #" + cameraMode + ": " + cameraModes[cameraMode-1]);
  cameraDevice = new Capture(this, cameraModes[cameraMode-1]);
  cameraDevice.start();
  initialized = true;
}


// Draws a quick text-based menu to the screen
void drawMenuScreen() {
  int y=10;
  text("Press a number key to select an input mode", 20, y);
  y += 40;
  text("O: Offline mode, test with TextRainInput.mov movie file instead of live camera feed.", 20, y);
  y += 40; 
  for (int i = 0; i < min(9,cameraModes.length); i++) {
    text(i+1 + ": " + cameraModes[i], 20, y);
    y += 40;
  }  
}

//Letter class
//Usage: to store information of each letter on the screen so that the positions of the letters can change regardless of the other letters
public class Letter{
  //char storing the letter
  public char letter;
  //int storing the ypos of the lettter
  public int ypos;
  //int storing how fast the letter falls
  public int velocity;
  Letter(char letter1){
    letter = letter1;
    ypos = 10;
    velocity = 1;
  }
  Letter(){
    letter = 'a';
    ypos = 10;
    velocity = 1;
  }
}
