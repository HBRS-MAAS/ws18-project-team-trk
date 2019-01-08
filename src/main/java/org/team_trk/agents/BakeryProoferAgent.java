package org.team_trk.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.team_trk.behaviours.ProductProcesser;
import org.team_trk.behaviours.ProductReceiver;
import org.team_trk.behaviours.ProductSender;
import org.team_trk.domain.Product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class BakeryProoferAgent extends BaseAgent {
	private static final long serialVersionUID = -5310054528477305012L;

	// Put agent initializations here
	protected void setup() {
		// Register service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery-proofer");
		sd.setName("JADE-bakery-proofer");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		List<CooledProduct> out = Arrays.asList(new CooledProduct("Bagel", 5), new CooledProduct("Donut", 1),
				new CooledProduct("Donut", 1), new CooledProduct("Berliner", 10), new CooledProduct("Muffin", 1),
				new CooledProduct("Donut", 1), new CooledProduct("Bagel", 5), new CooledProduct("Bread", 7),
				new CooledProduct("Muffin", 5), new CooledProduct("Donut", 1));
		
		AID aid2 = null;

		// Add the behaviour serving requests for offer from buyer agents
		addBehaviour(new CyclicBehaviour(this) {

			@Override
			public void action() {
				int random = (int) (Math.random() * out.size() / 4);
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				cfp.addReceiver(aid2);
				Gson gsonBuilder = new GsonBuilder().create();
				String jsonFromPojo = gsonBuilder.toJson(out.get(0));
				cfp.setContent(jsonFromPojo);
				cfp.setConversationId("packaged-orders");
				sendMessage(cfp);
			}
		});
	}

	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Proofer " + getAID().getName() + " terminating.");
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	class CooledProduct {

		private String type;
		private int quantity;

		public CooledProduct() {
			this("", 0);
		}

		public CooledProduct(String type, int quantity) {
			this.type = type;
			this.quantity = quantity;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public void addQuantity(int quantity) {
			this.quantity += quantity;
		}

	}

}
