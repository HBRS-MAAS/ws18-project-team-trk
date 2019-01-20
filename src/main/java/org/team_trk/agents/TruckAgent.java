package org.team_trk.agents;

import java.util.List;

import org.team_trk.behaviours.PurchaseOrdersServer;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TruckAgent extends BaseAgent {
	private static final long serialVersionUID = -5310054528477305012L;
	// The catalogue of books for sale (maps the title of a book to its price)
	private List<Object> content;

	// Put agent initializations here
	protected void setup() {
		// Register service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("truck");
		sd.setName("JADE-truck");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving requests for offer from buyer agents
//		addBehaviour(new OfferRequestsServer(recipes));
		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer(content));
	}

	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Bakery-agent " + getAID().getName() + " terminating.");
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

}