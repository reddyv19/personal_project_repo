#include "robot.h"
#include "json_helper.h"
#include <cstdlib>
#include <iostream>
#include "smartpath.h"

namespace csci3081 {


Robot::Robot(std::vector<float> pos, std::vector<float> direction, double speed, double radius, const picojson::object& details){

	for (int i=0; i < pos.size();i++){
		if (i==1){
			this->position.push_back(pos[i]+12);
		}
		else{
			this->position.push_back(pos[i]);
		}

	}

	for (int i=0; i < direction.size();i++){
	  this->direction.push_back(direction[i]);
	}
	this->radius = radius;
	this->speed = speed;
	this->type = "Robot";
	this->pickedUpPackage = false;
	this->Dynamic = true;
	this->battery = new Battery();
	this->StrategyPath = new SmartPath();
	StrategyPath->SetObject(this);
	details_ = details;
}

Robot::~Robot(){delete battery;}

void Robot::UpdatePosition(float dt){
	if(packages.size() <= 0){
		return;
	}

	//Robot is out of battery, remove all packages
	if (battery->IsDead()){
		if(currPackages.size() >= 1){
			picojson::object obj1 = JsonHelper::CreateJsonNotification();
			JsonHelper::AddStringToJsonObject(obj1, "value", "idle");
			for (int i = 0; i < observers.size(); i++){
				observers[i]->OnEvent(JsonHelper::ConvertPicojsonObjectToValue(obj1), *this);
			}

			//Drop the packages to the ground
			for (int i=0; i < currPackages.size(); i++){
				std::vector <float> tempPackLoc;
				tempPackLoc.push_back(this->currPackages[i]->GetPosition().at(0));
				tempPackLoc.push_back(this->currPackages[i]->GetStartPosition().at(1));
				tempPackLoc.push_back(this->currPackages[i]->GetPosition().at(2));
				this->currPackages[i]->UpdatePosition(tempPackLoc);
			}
		}
	
		currPackages.clear(); 
		RemovePackages();
		return;
	}
	battery->DepleteBattery(dt);
	Vector3D vec;
	//Robot is still picking up Packages
	if (!GoToCustomer){
		float distance = vec.Distance(this->position, this->package->GetPosition());
		//Robot can pick up the next package
		if (distance < this->package->GetRadius()){
			this->currPackages.push_back(this->package);
			currentCarrying+=this->package->GetWeight();
			picojson::object obj = JsonHelper::CreateJsonNotification();
			JsonHelper::AddStringToJsonObject(obj, "value", "en route");
			for (int j = 0; j < observers.size(); j++){
				observers[j]->OnEvent(JsonHelper::ConvertPicojsonObjectToValue(obj), *package);
			}
			SetPackage();
			return;
		}
		//Update Position
		else{
			float temp1 = vec.Distance(this->position, packageRoute.at(packageRouteStep - 1));
			if( temp1 <= .5) {
				packageRouteStep +=1;
			}
			Vector3D init(position);
			Vector3D update(packageRoute.at(packageRouteStep - 1));
			Vector3D change = update - init;
			change.Normalize();
			change.Scale(dt);
			float newspeed = this->maxSpeed - (this->maxSpeed/2)*(currentCarrying/carryingCapacity);
			if(newspeed >= speed){
				newspeed = speed;
			}
			change.Scale(newspeed);
			Vector3D newLoc = init + change;
			position.clear();
			for (int i=0; i < newLoc.GetVector().size();i++){
				this->position.push_back(newLoc.GetVector()[i]);
			}
			for (int i=0; i < currPackages.size(); i++){
				this->currPackages[i]->UpdatePosition(this->position);
			}
		}

	}
	else{
		float distance = vec.Distance(this->position, this->package->GetDestination());
		//Robot can drop off the current package
		if(distance < this->package->GetCustRadius()){
			this->package->Deliver();
			picojson::object obj = JsonHelper::CreateJsonNotification();
			JsonHelper::AddStringToJsonObject(obj, "value", "delivered");
			for (int i = 0; i < observers.size(); i++){
				observers[i]->OnEvent(JsonHelper::ConvertPicojsonObjectToValue(obj), *package);
			}
			this->currentCarrying -= this->package->GetWeight();
			this->packages.erase(this->packages.begin());
			this->currPackages.erase(this->currPackages.begin());
			GoToCustomerPath();
		}
		else{//Update position
			float temp2 = vec.Distance(this->position, customerRoute.at(customerRouteStep - 1));
			if(temp2 <= .5) {
				customerRouteStep +=1;
			}
			Vector3D init(position);
			Vector3D update(customerRoute.at(customerRouteStep - 1));
			Vector3D change = update - init;
			change.Normalize();
			change.Scale(dt);
			float newspeed = this->maxSpeed - (this->maxSpeed/2)*(currentCarrying/carryingCapacity);
			if(newspeed >= speed){
				newspeed = speed;
			}
			change.Scale(newspeed);
			Vector3D newLoc = init + change;
			position.clear();
			for (int i=0; i < newLoc.GetVector().size();i++){
				this->position.push_back(newLoc.GetVector()[i]);
			}
			for (int i=0; i < currPackages.size(); i++){
				this->currPackages[i]->UpdatePosition(this->position);
			}
		}
	}
}

void Robot::SetPackage(){
	if(this->packages.size() >= 1) {
		//If robot cannot pick up more packages, start to drop them off
		if ((currPackages.size() == packages.size()) || currentCarrying + this->packages[currPackages.size()]->GetWeight() > carryingCapacity){
			GoToCustomer = true;
			GoToCustomerPath();
			return;
		}
		this->package = packages.at(currPackages.size());
		StrategyPath->UpdatePath();
		packageRouteStep = 1;
		picojson::object obj = JsonHelper::CreateJsonNotification();
		JsonHelper::AddStringToJsonObject(obj, "value", "moving");
		JsonHelper::AddStdVectorVectorFloatToJsonObject(obj, "path", packageRoute);
		for (int i = 0; i < observers.size(); i++){
			observers[i]->OnEvent(JsonHelper::ConvertPicojsonObjectToValue(obj), *this);
		}
	}
	else{
		picojson::object obj1 = JsonHelper::CreateJsonNotification();
		JsonHelper::AddStringToJsonObject(obj1, "value", "idle");
		for (int i = 0; i < observers.size(); i++){
		  observers[i]->OnEvent(JsonHelper::ConvertPicojsonObjectToValue(obj1), *this);
		}
	}
}
void Robot::GoToCustomerPath(){
	//If robot still has packages, set route to drop off the most current one
	if (currPackages.size() >= 1){
		this->package = currPackages[0];
		StrategyPath->UpdatePath();
		customerRouteStep = 1;
		picojson::object obj1 = JsonHelper::CreateJsonNotification();
		JsonHelper::AddStringToJsonObject(obj1, "value", "moving");
		JsonHelper::AddStdVectorVectorFloatToJsonObject(obj1, "path", customerRoute);
		for (int i = 0; i < observers.size(); i++){
			observers[i]->OnEvent(JsonHelper::ConvertPicojsonObjectToValue(obj1), *this);
		}
	}
	else{
		GoToCustomer = false;
		SetPackage();
	}

}

void Robot::AddPackage(Package* newPackage){
	this->packages.push_back(newPackage);
    if (packages.size() ==1){
        SetPackage();
    }
}


}
