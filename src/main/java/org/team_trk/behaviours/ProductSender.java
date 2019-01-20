package org.team_trk.behaviours;

import java.util.List;

import org.team_trk.domain.Product;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ProductSender extends CyclicBehaviour {
	private static final long serialVersionUID = -3863996398471466048L;

	private List<Product> products;
	private List<AID> to;
	private int step = 0;
	private MessageTemplate mt;

	public ProductSender(List<Product> products, List<AID> to) {
		this.products = products;
		this.to = to;
	}

	public void action() {
		switch (step) {
		case 0:
// Send the cfp to all sellers
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			/*
			 * for (int i = 0; i < to.size(); ++i) { cfp.addReceiver(to.get(i)); }
			 */
			if(to.isEmpty()) {
				block();
				break;
			}
			cfp.addReceiver(to.get(0));

			cfp.setContent(products.toString());
			cfp.setConversationId("bread-order");
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
			myAgent.send(cfp);
// Prepare the template to get proposals
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId(cfp.getConversationId()),
					MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
			step = 1;
			break;
		case 1:
// Receive all proposals/refusals from seller agents
			ACLMessage reply = myAgent.receive(mt);
			if (reply != null) {
// Reply received
				if (reply.getPerformative() == ACLMessage.CONFIRM) {
					products.remove(0);
					step = 0;
				}
			} else {
				block();
			}
			break;
		}
	}
}