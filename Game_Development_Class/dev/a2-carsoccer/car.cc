#include "car.h"
#include <cmath>

/// The constructor sets the static properties of the car, like its size,
/// and then calls Reset() to reset the position, velocity, and any other
/// dynamic variables that change during game play.
Car::Car() : size_(3,2,4), collision_radius_(2.5) {
    Reset();
}

Car::~Car() {
}


float Car::collision_radius() {
    return collision_radius_;
    
}

Vector3 Car::size() {
    return size_;
}

Point3 Car::position() {
    return position_;
}

void Car::set_position(const Point3 &p) {
    position_ = p;
    if(position_[0]>(40-collision_radius_)){
        position_[0] =40-collision_radius_;
    }
    if(position_[0]<(-40+collision_radius_)){
        position_[0] = -40+collision_radius_;
    }
    if(position_[2]>50-collision_radius_){
        position_[2] = 50-collision_radius_;
    }
    if(position_[2]<-50+collision_radius_){
        position_[2] = -50+collision_radius_;
    }
    
}

Vector3 Car::forward() {
    return forward_;
}

void Car::set_forward(const Vector3 &v, float dt) {
    std::cout << "time: "<<dt << std::endl;
    if(v[2]==0){
        if(forward_[2]<(float).1&&forward_[2]>(float)-.1){
            forward_[2] = 0;
        }
        if(forward_[2]>0){
            forward_[2] = forward_[2]-dt*accel_;
        }else if(forward_[2]<0){
            forward_[2] = forward_[2]+dt*accel_;
        }
    }else{
        forward_[2] = forward_[2]+v[2]*dt*accel_;
        if(forward_[2]>2.0){
            forward_[2] = 2.0;
        }
        if(forward_[2]<-2.0){
            forward_[2] = -2.0;
        }
    }
    if(v[0]==0){
        forward_[0]=v[0];
    }else if(forward_[2]!=0){
        turn_ = turn_+dt*(acos(0)/4);
        
        forward_[0]=v[0];
    }
}

float Car::speed() {
    return speed_;
}

void Car::set_speed(float s) {
    speed_ = s;
}

Vector3 Car::velocity() {
    std::cout << "turn: "<<turn_ << std::endl;
    forward_[0] = forward_[0]*cos(turn_)-forward_[2]*sin(turn_);
    forward_[2] = forward_[0]*sin(turn_)+forward_[2]*cos(turn_);  
    std::cout << "x: "<< forward_[0] << " z: " <<forward_[2] <<std::endl;
    return speed_ * forward_;
}


void Car::Reset() {
    position_ = Point3(0, size_[1]/2, 45);
    forward_ = Vector3(0, 0, -1);
    speed_ = 20.0f;
}

void Car::Draw(QuickShapes quickShapes, Matrix4 modelMatrix, Matrix4 viewMatrix, Matrix4 projMatrix) {
    Color carcol(0.8f, 0.2f, 0.2f);
    Matrix4 Mcar =
        Matrix4::Translation(position_ - Point3(0,0,0)) *
        Matrix4::LookAt(Point3(0,0,0), Point3(0,0,0) + forward_, Vector3(0,1,0)).Inverse() *
        Matrix4::Scale(size_) *
        Matrix4::Scale(Vector3(0.5f, 0.5f, 0.5f));
    quickShapes.DrawCube(modelMatrix * Mcar, viewMatrix, projMatrix, carcol);
}
