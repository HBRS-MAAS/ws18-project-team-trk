package org.team_trk.behaviours;

import java.util.List;

import org.team_trk.domain.Product;

import com.google.gson.Gson;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ProductReceiver extends CyclicBehaviour {
	private static final long serialVersionUID = -3863996398471466048L;

	private List<Product> products;

	public ProductReceiver(List<Product> products) {
		this.products = products;
	}

	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
// Message received. Process it
			String msgContent = msg.getContent();
			Product product = new Gson().fromJson(msgContent, Product.class);
			products.add(product);
			System.out.println("Got request for order " + msgContent);

			ACLMessage reply = msg.createReply();
// The requested book is available for sale. Reply with the price
			reply.setPerformative(ACLMessage.CONFIRM);
			myAgent.send(reply);
			System.out.println("Send reply " + ACLMessage.getPerformative(reply.getPerformative())
					+ " to request on order " + msgContent);
		} else {
			block();
		}
	}
}