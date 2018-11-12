package org.team_trk.agents;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.team_trk.behaviours.OfferRequestsServer;
import org.team_trk.behaviours.ProductProcesser;
import org.team_trk.behaviours.ProductReceiver;
import org.team_trk.behaviours.ProductSender;
import org.team_trk.behaviours.PurchaseOrdersServer;
import org.team_trk.domain.Product;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BakeryKneadingAgent extends Agent {
	private static final long serialVersionUID = -5310054528477305012L;
	private List<Object> content;

	// Put agent initializations here
	protected void setup() {
		// Register service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery-kneading-machine");
		sd.setName("JADE-bakery-kneading-machine");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		List<Product> in = new ArrayList<>();
		List<Product> out = new ArrayList<>();

		// Add the behaviour serving requests for offer from buyer agents
		addBehaviour(new ProductReceiver(in));

		addBehaviour(new ProductProcesser(() -> {
			out.addAll(in);
			in.clear();
		}));
		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new ProductSender(out, new ArrayList<>()));
	}

	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Kneading machine " + getAID().getName() + " terminating.");
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

}