/**
 * @file customer.h
 */
#ifndef CUSTOMER_H_
#define CUSTOMER_H_
/*******************************************************************************
 * Includes
 ******************************************************************************/
#include "entity_base.h"
#include <vector>
#include <string>
#include "vector2D.h"

namespace csci3081 {
/*******************************************************************************
 * Class Definitions
 ******************************************************************************/
	/**
	 * @brief This class holds the basic constructor for a customer.
	 *
	 * In the grand scheme of things, customers really don't do much. They hold a position for where packages need to be delivered to and they show up in that
	 * position during the simulation.
	 */
    class Customer : public csci3081::EntityBase {
        public:
        /**
		 * @brief This is the constructor for the Customer class. It takes in 1 float vector and a picojson object.
		 * 
		 * This is the general constructor for the Customer. It initializes all of the customer's attributes using the
		 * input parameters. The first parameter is the customer's location vector. The next, and last, parameter is the
         * Customer's picojson object itself.
		 * 
		 * @param[in] location location vector for the Customer. 
		 * @param[in] picojson The customer's picojson object. 
		 * 
		 */
        Customer(std::vector<float> location, const picojson::object& obj);

		/**
		 * @brief This is the default constructor for the Customer Class
		 * 
		 * This will mainly be used when instantiating stand alone customers before setting them to a specified one
		 * 
		 */
		Customer();

		/**
		 * @brief This is the Destructor for a Customer object
		 */
		~Customer();
        
        /**
		 * @brief This is function simply returns the location vector for a given Customer.
		 * 
		 * This function takes in no inputs and will return the customer's location vector.
		 * 
		 * @return Customer Location Vector
		 */
		const std::vector<float>& GetDirection() const;

		/**
		 * @brief This is function simply exists to satisfy inheritance.
		 * 
		 * This function is also used in the simulation to access the position of the customer within the simulation
		 * 
		 * @return Customer Position Vector
		 */
		const std::vector<float>& GetPosition() const;
		/**
		* @brief This function is called to see if the drone is in the correct position foor drop off
		* 
		* It is used to know if the delivery state of simluation should change
		* 
		* @return a float value representing the dropooff radius of the customer
		*/
		float GetRadius();
    };

}
#endif