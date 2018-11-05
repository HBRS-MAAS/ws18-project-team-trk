package org.team_trk.agents;

import java.awt.print.Book;
import java.util.Hashtable;

import org.team_trk.behaviours.OfferRequestsServer;
import org.team_trk.behaviours.PurchaseOrdersServer;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BakeryProcessingAgent extends Agent {
	private static final long serialVersionUID = -5310054528477305012L;
	// The catalogue of books for sale (maps the title of a book to its price)
	private Hashtable<String, Book> catalogue;

	// Put agent initializations here
	protected void setup() {
		// Create the catalogue
//		catalogue = new Hashtable<>();
//
//		Object[] args = getArguments();
//		if (args != null && args.length > 0) {
//			for (Object arg : args) {
//				Book b = (Book) arg;
//				catalogue.put(b.getTitle(), b);
//				// Paperbacks could be less expensive than ebooks to make sure some are bought
//				b.setPrice((Math.random() * 20 + 5) * ((b.getBookType() == BookType.PAPERBACK) ? 1 : 2));
//			}
//		}

		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery");
		sd.setName("JADE-bakery-processor");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving requests for offer from buyer agents
		addBehaviour(new OfferRequestsServer());
		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer());
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