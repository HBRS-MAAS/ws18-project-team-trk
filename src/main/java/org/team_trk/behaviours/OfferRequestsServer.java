package org.team_trk.behaviours;

import org.json.JSONObject;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class OfferRequestsServer extends CyclicBehaviour {
	private static final long serialVersionUID = -3863996398471466048L;

	public OfferRequestsServer() {
	}

	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
// Message received. Process it
			String msgContent = msg.getContent();
			JSONObject order = new JSONObject(msgContent);
			System.out.println("Got request for order " + order);

			ACLMessage reply = msg.createReply();
			Double price = Math.random() * 20;
			if (price != null && price != 0) {
// The requested book is available for sale. Reply with the price
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent(String.valueOf(price.intValue()));
			} else {
// The requested book is NOT available for sale.
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setContent("not-available");
			}
			myAgent.send(reply);
			System.out.println("Send reply " + ACLMessage.getPerformative(reply.getPerformative())
					+ " to request on order " + order);
		} else {
			block();
		}
	}
}