/**
 * @file delivery_simulation.h
 */
#ifndef DELIVERY_SIMULATION_H_
#define DELIVERY_SIMULATION_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <EntityProject/facade/delivery_system.h>
#include <vector>
#include <string>
#include "compositefactory.h"
#include "dronefactory.h"
#include "customerfactory.h"
#include "packagefactory.h"
#include "robotfactory.h"



namespace csci3081 {

/*******************************************************************************
 * Class Definitions
 ******************************************************************************/
	/**
	 * @brief This is the facade for the delivery system
	 *
	 * This class will delegate operations for the whole drone delivery system.
	 * See the documentation for IDeliverySystem for more information.
	 */

	// DO NOT MODIFY (unless you really know what you are doing)
	class DeliverySimulation : public IDeliverySystem {
		public:
			/**
			 * @brief Constructor: this can do any setup your system necessitates.
			 */
			DeliverySimulation();

			/// Desconstructor: This should free any memory that your program uses.
			~DeliverySimulation();

			/**
			Given the picojson::object val, this should create an entity.
			Based on the type of entity, there may be different fields.
			You can see the vals that will be passed in the project/web/scenes
				directory. Some of the fields are for our backend system and you don't
				need to worry about them. (for instance, mesh, rotation, offset, etc.)

			Some fields in val that you will need to create the entity correctly:

				type: string (could be "drone/customer/package")

				name: string

				position: array (contains [x_position, y_position, z_position])

				direction: array (contains [x, y, z])

				speed: float

				battery_capacity: float

			Don't add the entity to the simulation until it is passed in via AddEntity
			*/
			IEntity* CreateEntity(const picojson::object& val);

			/**
			 *  This function should add a factory to the composite factory pattern
			 */
			void AddFactory(IEntityFactory* factory);

			/**
			 *  This function should add an entity to the simulation
			 */
			void AddEntity(IEntity* entity);

			/**
			This function should simply store a reference to the IGraph* somewhere.
			The IGraph contains useful functions such as the GetPath function which can
			be used to get a path from one position to another.
			*/
			void SetGraph(const IGraph* graph);

			/**
			This function tells the simulation that the IEntity* package should be delivered
			to the IEntity* dest (which is likely a customer). How the delivery takes place
			is entirely dependent on how you design your code, but it should involve a drone
			navigating to the package, picking it up, and then moving to the customer and
			dropping the package.
			*/
			void ScheduleDelivery(IEntity* package, IEntity* dest);

			/** Observer functions will not be used in iteration1 */
			void AddObserver(IEntityObserver* observer);

			/** Observer functions will not be used in iteration1 */
			void RemoveObserver(IEntityObserver* observer);

			/**
			GetEntities should return all entities that have been ADDED to the system
			*/
			const std::vector<IEntity*>& GetEntities() const;

			/**
			GetObservers should returns the observers vector that contains all the observers.
			*/
			std::vector<IEntityObserver*> GetObservers();

			/**
			This function is used to advance time in the simulation. float dt refers to the
			amount of time the update call should advance the simulation by. For instance if a drone
			moves 1 unit of distance per unit of time, and Update is called with dt=.05, then the
			drone should move 1 * .05 = .05 units of distance.

			Some things that should happen in the Update function: move drones, check if
			packages have been delivered to customers, etc.
			*/

			void Update(float dt);

			/**
			 * @brief You do not need to worry about this function
			 *
			 * This function takes care of turning json into function calls of your system.
			 * YOU DO NOT NEED TO IMPLEMENT THIS
			 *it is already implemented in the delivery_simulation.cc we have provided.
			*/
			void RunScript(const picojson::array& script, IEntitySystem* system) const;

		private:
			// You don't strictly need to use the following variable, but it is probably
			// the most straightforward way of storing the entities in the system.
			// Feel free to use it as is or change it.
			std::vector<IEntity*> entities_;
			const IGraph* g;
			CompositeFactory* factories_;
			int countId = 0;
			std::vector<IEntityObserver*> observers;
	};

}  // namespace csci3081


#endif  // DELIVERY_SIMULATION_H_
