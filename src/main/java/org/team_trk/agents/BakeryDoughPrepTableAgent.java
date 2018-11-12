package org.team_trk.agents;

import java.util.List;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BakeryDoughPrepTableAgent extends Agent {
	private static final long serialVersionUID = -5310054528477305012L;
	private List<Object> content;

	// Put agent initializations here
	protected void setup() {
		getAID().addUserDefinedSlot("location", here().getID());
		// Register service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery-prep-table");
		sd.setName("JADE-bakery-prep-table");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
//
//		// Add the behaviour serving requests for offer from buyer agents
//		addBehaviour(new OfferRequestsServer(recipes));
//		// Add the behaviour serving purchase orders from buyer agents
//		addBehaviour(new PurchaseOrdersServer(content));
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