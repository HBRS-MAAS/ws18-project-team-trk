# MAAS Project - Team-TRK

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b65a987014c498da2df253ff30a0d4a)](https://app.codacy.com/app/tmeule2s/ws18-project-team-trk?utm_source=github.com&utm_medium=referral&utm_content=HBRS-MAAS/ws18-project-team-trk&utm_campaign=Badge_Grade_Dashboard)
Master [![Build Status](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk.svg?branch=master)](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk)

Develop [![Build Status](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk.svg?branch=develop)](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk)

The project contains a customer that tries to buy different products from a bakery and the bakery that contains multiple agents that process the order and the products. Next to these agents there is a timekeeper that controls the flow of time for theother agents.

For testing purposes there has been added a dummy agent that passes "finished products" to the packaging stage.

After every agent is running the customer starts to communicate with the bakeries about a possible order and asks for a price. After the customer has decided to buy a product from a bakery it sends the buy request. The bakery then accepts it via the order processing agent that manages the communication with the customer and sends the info about a new order to the scheduler who broadcasts it to all other agents of this bakery. At some point the dummy agent starts to send a few of the needed products to the packaging stage. The packaging takes in those "finished products" and packages them into boxes corresponding to the given orders. After a Box is filled those boxes are sent to the loading bay.

Until now this is all that is happening because the rest of the agents were not part of this group so this project only uses the agents that are needed to show off the packaging stage.

## Team Members
* Tim van der Meulen - [@tmeule2s](https://github.com/tmeule2s)

## Dependencies
* JADE v.4.5.0
* ...

## How to run
Just install gradle and run:

    gradle run

It will automatically get the dependencies and start JADE with the configured agents.
In case you want to clean you workspace run

    gradle clean

### Parameter

-host

	the parameter defined after this flag is used as ip address of the host computer.

	
-port

	the parameter defined after this flag is used as the port on the host computer


scenario path

	To change the scenario you can pass a parameter with the path to the scenario folder. Any parameter without one of the a previous parameter flags in front is interpreted as scenario path!

## Eclipse
To use this project with eclipse run

    gradle eclipse

This command will create the necessary eclipse files.
Afterwards you can import the project folder.

## Design Decisions
Here you can find out why the code is written the way it is.

### Start method
The start method uses the java jade api to create a container in the jade runtime env and starts the needed agents in that container. I used the api because it is much more readable than the command line strings used by the rest of the class. You can easily change the host by changing the code line that defines the profile for that and you can pass whole objects as parameters instead of parsing the needed information in a String and then parsing it back in the agent.
I tried to pass the information every agent needs as a parameter so the agents do not need to know which scenario is played but I did not want to change the code of the agents that are not mine.

### Packaging Agent
As mentioned above the agents takes in a map as parameter that contains the configuration about the products per box limits that are used.
The attributes are:

- Map<String, Integer> productsPerBox;

	This map contains all the products to be packaged as keys and the corresponding limit as values. This config is set by the first parameter of this agent.

- List<Order> orders;

	This list contains all the orders that were propagated by the scheduler.

- Map<String, CooledProduct> availableProducts;

	This map contains the product type as key and the cooled product as value for every product that is pased by the previous stage.

- List<OutObject> out;

	This list contains all the objects that should be sent to the next stage. The format of the message content can be found under messages.

- AID loadingBayAID;

	This aid contains the aid of the next stage. In this case this should be the loading bay.
	
The logic of the agent then just waits for a broadcast by the scheduler to save this info in the order list, listens to incoming finished products and checks if there are more products of a certain type in availableProducts than the first order needs. If the first order does not need anymore the second one is checked. When there are more or an equal amount of products needed and available those are packaged into one or more boxed, depending on the amount of products and the box size. In the last step the packaging agent sends all boxes grouped by their order id to the next stage.
	
#### Messages
in (cooled product with amount)

	{
		"type" : "value",
		"quantity" : "value"
	}
	
out (list of boxes corresponding to an order id)

	{
		"OrderID" : "value"
		"Boxes" : [
			{
				"ProductType" : "value",
				"Quantity" : "value",
				"BoxID" : value
			},
			{
				...
			}
		]
	}

#### Objects
All the objects here are private class objects because I think a logical structure for a multi agent system can be done like a microservice attempt. Every agents contains the classes he needs himself and just parses them with json for input and output. This way every agent saves only the attributes of a message he needs and ignores the rest of it. When you do it like that you do not have to worry to change an object as long the communication with the agent is well defined so that the other agents know what they need to send as input. The point where this gets the cleares is when the scheduler broadcasts the info about a new order. The packaging agents just saves the attributes he needs and ignores the rest of the information.

The used objects are:

CooledProduct

	The parsed class for incoming info about a finished product.

OutObject

	The parsed class for outgoing information about one or more packed boxes of products that belong to a certain order.

Order

	The parsed class for broadcast info about a new order by the scheduler.

Box

	The class that is used to package the products into. This is also used by OutObject to represent a box in the outgoing list of boxes.