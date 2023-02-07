/** CSci-4611 Assignment 2:  Car Soccer
 */

#include "car_soccer.h"
#include "config.h"
#include <string>
#include <iostream>

// Remember in C++, the .h file list all the functions and member variables that are part of the class!
// Look there first to understand what is part of the CarSoccer class, then look below to see how each
// function is implemented.


CarSoccer::CarSoccer() : GraphicsApp(1024,768, "Car Soccer") {
    // If you are having trouble driving the car with the keybaord, you can set this to true to use
    // the mouse instead.  The mouse controls are based on the postion of the mouse cursor on the window.
    // There is a "dead zone" in the middle of the window, and if you move the mouse up/down or left/right
    // outside of that zone, it is like pushing the up/down and/or left/right keys on the keyboard
    use_mouse_ = false;
        
    // Define a search path for finding data files (images and shaders)
    searchPath_.push_back(".");
    searchPath_.push_back("./data");
    searchPath_.push_back(DATA_DIR_INSTALL);
    searchPath_.push_back(DATA_DIR_BUILD);
}

CarSoccer::~CarSoccer() {
}


void CarSoccer::OnMouseMove(const Point2& pos, const Vector2& delta)
{
    mouse_pos_ = PixelsToNormalizedDeviceCoords(pos);
}

void CarSoccer::OnSpecialKeyDown(int key, int scancode, int modifiers) {
    if (key == GLFW_KEY_SPACE) {
        // Here's where you could call some form of launch_ball();
        ball_.Reset();
    }
}

/// This is a little utility function that is helpful.  It treats the arrow keys like a joystick or D-pad on a game controller
/// and returns the direction you are pressing as a 2D vector, taking into account the fact that you might be holding
/// down more than one key at a time.
Vector2 CarSoccer::joystick_direction() {
    Vector2 dir;

    if (use_mouse_) {
        // threshold defines the size of the "dead zone" in the middle of the screen
        // if the mouse's x,y position falls outside of this, then it is like pushing
        // the corresponding key on the keyboard
        const float threshold = 0.2f;
        dir[0] = 0;
        if (mouse_pos_[0] < -threshold) {
            dir[0] = -1;
        }
        else if (mouse_pos_[0] > threshold) {
            dir[0] = 1;
        }
        dir[1] = 0;
        if (mouse_pos_[1] < -threshold) {
            dir[1] = -1;
        }
        else if (mouse_pos_[1] > threshold) {
            dir[1] = 1;
        }
    }
    else {
        // the default user interface is to use the arrow keys on the keyboard.
        // like a D-pad on a game controller, you can hold more than one key down at a time if you want.
        if (IsKeyDown(GLFW_KEY_LEFT))
            dir[0]--;
        if (IsKeyDown(GLFW_KEY_RIGHT))
            dir[0]++;
        if (IsKeyDown(GLFW_KEY_UP))
            dir[1]++;
        if (IsKeyDown(GLFW_KEY_DOWN))
            dir[1]--;
    }

    return dir;
}

// dt is for "Delta Time", the elapsed time in seconds since the last frame
void CarSoccer::UpdateSimulation(double dt) {
    Vector2 dpad_dir = joystick_direction();
    std::cout << "D-Pad Direction: " << dpad_dir << std::endl;

    // Here's where you shound do your "simulation", updating the positions of the
    // car and ball based on the elapsed time and checking for collisions.  Filling
    // in this routine is the main part of the assignment.

    // Example: This is not the "correct way" to drive the car, but this code
    // will at least move the car around for testing

    //two if statements used to detect whether the ball hits the goal
    if(ball_.position().x()<10.0-2.6&&ball_.position().x()>-10.0+2.6&&ball_.position().y()<8.0-2.6&&ball_.position().z()>47.4){
        ball_.Reset();
    }
    if(ball_.position().x()<10.0-2.6&&ball_.position().x()>-10.0+2.6&&ball_.position().y()<8.0-2.6&&ball_.position().z()<-47.6){
        ball_.Reset();
    }

    //six if statements below used to detect whether ball is hitting the border
    if (ball_.position().x() > 40){
        Vector3 norm(-40, 0, 0);
        norm = norm.ToUnit();
        //formula below is represent r = v - 2*(v.n)*n
        Vector3 vel = (ball_.velocity() - 2*(ball_.velocity().Dot(norm))*norm);
        //multiply by .8 for friction
        ball_.set_velocity(vel*.8);
        ball_.set_position(Point3(39,ball_.position()[1],ball_.position()[2]));
    }
    //logic above applies for all borders except for ground logic explained on line 131
    if (ball_.position().x() < -40){
        Vector3 norm(40, 0, 0);
        norm = norm.ToUnit();
        Vector3 vel = (ball_.velocity() - 2*(ball_.velocity().Dot(norm))*norm);
        ball_.set_velocity(vel*.8);
        ball_.set_position(Point3(-39,ball_.position()[1],ball_.position()[2]));
    }
    if (ball_.position().y() > 35){
        Vector3 norm(0, -35, 0);
        norm = norm.ToUnit();
        Vector3 vel = (ball_.velocity() - 2*(ball_.velocity().Dot(norm))*norm);
        ball_.set_velocity(vel*.8);
        ball_.set_position(Point3(ball_.position()[0],34,ball_.position()[2]));
    }
    //friction is set to .7 for the ground due to the higher friction of the turf
    if (ball_.position().y() < 2.5){
       Vector3 norm(0, 1, 0);
       Vector3 vel = (ball_.velocity() - 1.4*(ball_.velocity().Dot(norm))*norm);
       ball_.set_velocity(vel);
       ball_.set_position(Point3(ball_.position()[0],2.6,ball_.position()[2]));
    }
    if (ball_.position().z() > 50){
        Vector3 norm(0, 0, -50);
        norm = norm.ToUnit();
        Vector3 vel = (ball_.velocity() - 2*(ball_.velocity().Dot(norm))*norm);
        ball_.set_position(Point3(ball_.position()[0],ball_.position()[1],49));
        ball_.set_velocity(vel*.8);
    }
    if (ball_.position().z() < -50){
        Vector3 norm(0, 0, 50);
        norm = norm.ToUnit();
        Vector3 vel = (ball_.velocity() - 2*(ball_.velocity().Dot(norm))*norm);
        ball_.set_position(Point3(ball_.position()[0],ball_.position()[1],-49));
        ball_.set_velocity(vel*.8);
    }

    car_.set_forward(Vector3(dpad_dir[0], 0, -dpad_dir[1]), dt);

    //collision detection, calls helper function isCollide to dectect collision
    if(isCollide()){
        //using the ball-car collision algorithm found in assignment write-up
        Vector3 vrel = ball_.velocity() -car_.velocity();
        Vector3 norm = ball_.position()-car_.position();
        norm = norm.ToUnit();
        vrel = vrel - 2*(vrel.Dot(norm))*norm;
        Vector3 vcar = car_.velocity();
        ball_.set_velocity(vcar+vrel);
    }
    
    car_.set_position(car_.position() + car_.velocity() * dt);
    //calls helper function gravity to simulate acceleration due to gravity
    ball_.Gravity(dt);

    Vector3 balVal = ball_.velocity();
    ball_.set_position(ball_.position() + Vector3(balVal.x(), balVal.y(), balVal.z()) * dt);
}

bool CarSoccer::isCollide() {
    //use collision detection algorithm to find if ball and car are in radius
    float combined_rad = ball_.radius()+car_.collision_radius();
    Vector3 distVect = ball_.position()-car_.position();
    float dist = distVect.Length();
    if(dist>combined_rad){
        return false;
    }else{
        return true;
    }
}
void CarSoccer::InitOpenGL() {
    // Set up the camera in a good position to see the entire field
    projMatrix_ = Matrix4::Perspective(60, aspect_ratio(), 1, 1000);
    modelMatrix_ = Matrix4::LookAt(Point3(0,60,70), Point3(0,0,10), Vector3(0,1,0));
 
    // Set a background color for the screen (don't worry if you get a depricated warning on this line in OSX)
    glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
    
    // Load some image files we'll use
    fieldTex_.InitFromFile(Platform::FindFile("pitch.png", searchPath_));
    crowdTex_.InitFromFile(Platform::FindFile("crowd.png", searchPath_));
}


void CarSoccer::DrawUsingOpenGL() {
    // Draw the crowd as a fullscreen background image
    quickShapes_.DrawFullscreenTexture(Color(1,1,1), crowdTex_);

    // Draw the car and the ball
    car_.Draw(quickShapes_, modelMatrix_, viewMatrix_, projMatrix_);
    ball_.Draw(quickShapes_, modelMatrix_, viewMatrix_, projMatrix_);
    
    // Draw the field with the field texture on it.
    Color col(16.0f/255.0f, 46.0f/255.0f, 9.0f/255.0f);
    Matrix4 M = Matrix4::Translation(Vector3(0.0f, -0.201f, 0.0f)) * Matrix4::Scale(Vector3(50.0f, 1.0f, 60.0f));
    quickShapes_.DrawSquare(modelMatrix_ * M, viewMatrix_, projMatrix_, col);
    M = Matrix4::Translation(Vector3(0.0f, -0.2f, 0.0f)) * Matrix4::Scale(Vector3(40.0f, 1.0f, 50.0f));
    quickShapes_.DrawSquare(modelMatrix_ * M, viewMatrix_, projMatrix_, Color(1,1,1), fieldTex_);
    
    // You should add drawing the goals and the boundary of the playing area
    std::vector<Point3> goalLines1;
    std::vector<Point3> goalLines2;
    for (float i = 0; i < 21; i=i+1) {
        goalLines1.push_back(Point3(-10+i, 0 , 50.0));
        goalLines1.push_back(Point3(-10 + i, 8, 50.0));
    }
    for (float i = 0; i < 21; i = i + 1) {
        goalLines2.push_back(Point3(-10+i, 0, -50.0));
        goalLines2.push_back(Point3(-10+i, 8, -50.0));
    }
    for (float i = 0; i < 8.5; i = i + .5) {
        goalLines1.push_back(Point3(-10, i, 50.0));
        goalLines1.push_back(Point3(10, i, 50.0));
    }
    for (float i = 0; i < 8.5; i = i + .5) {
        goalLines2.push_back(Point3(-10, i, -50.0));
        goalLines2.push_back(Point3(10, i, -50.0));
    }

    quickShapes_.DrawLines(modelMatrix_, viewMatrix_, projMatrix_, Color(0, 0, 255), goalLines1, QuickShapes::LinesType::LINES, .1);
    quickShapes_.DrawLines(modelMatrix_, viewMatrix_, projMatrix_, Color(255, 0, 0), goalLines2, QuickShapes::LinesType::LINES, .1);
    
}
