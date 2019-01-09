package org.team_trk.behaviours;

import org.json.JSONObject;
import org.team_trk.agents.BaseAgentOld;
import org.team_trk.domain.BreadOrder;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestPerformer extends Behaviour {

	private static final long serialVersionUID = -147484173252708723L;

	private BaseAgentOld baseAgent;

	private AID bestSeller; // The agent who provides the best offer
	private int bestPrice; // The best offered price
	private int repliesCnt = 0; // The counter of replies from seller agents
	private MessageTemplate mt; // The template to receive replies
	private int step = 0;

	private AID[] sellerAgents;
	private JSONObject breadOrder;

	public RequestPerformer(BreadOrder breadOrder, AID[] sellerAgents) {
		this.breadOrder = new JSONObject(breadOrder);
		this.sellerAgents = sellerAgents;
	}

	public void action() {
		if (baseAgent == null) {
			baseAgent = (BaseAgentOld) myAgent;
		}
		switch (step) {
		case 0:
// Send the cfp to all sellers
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			for (int i = 0; i < sellerAgents.length; ++i) {
				cfp.addReceiver(sellerAgents[i]);
			}

			cfp.setContent(breadOrder.toString());
			cfp.setConversationId("bread-order");
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
			baseAgent.sendMessage(cfp);
			System.out.println(String.format("Send buy request for bread order %s to all sellers.", breadOrder));
// Prepare the template to get proposals
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId(cfp.getConversationId()),
					MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
			step = 1;
			break;
		case 1:
// Receive all proposals/refusals from seller agents
			ACLMessage reply = baseAgent.receiveMessage(mt);
			if (reply != null) {
// Reply received
				if (reply.getPerformative() == ACLMessage.PROPOSE) {
// This is an offer
					int price = Integer.parseInt(reply.getContent());
					if (bestSeller == null || price < bestPrice) {
// This is the best offer at present
						bestPrice = price;
						bestSeller = reply.getSender();
					}
				}
				repliesCnt++;
				if (repliesCnt >= sellerAgents.length) {
// We received all replies
					step = 2;
				}
			} else {
				block();
			}
			break;
		case 2:
			// Send the purchase order to the seller that provided the best offer
			ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			order.addReceiver(bestSeller);

			order.setContent(breadOrder.toString());
			order.setConversationId("bread-order");
			order.setReplyWith("order" + System.currentTimeMillis());
			baseAgent.sendMessage(order);
			System.out.println("Accepted proposalfor " + breadOrder + " from " + bestSeller);
			// Prepare the template to get the purchase order reply
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId(order.getConversationId()),
					MessageTemplate.MatchInReplyTo(order.getReplyWith()));
			step = 3;
			break;
		case 3:
			// Receive the purchase order reply
			reply = baseAgent.receiveMessage(mt);
			if (reply != null) {
				// Purchase order reply received
				if (reply.getPerformative() == ACLMessage.INFORM) {
					// Purchase successful. We can terminate
					System.out.println(breadOrder + " successfully purchased.");
					System.out.println("Price = " + bestPrice);
					myAgent.doDelete();
				}
				step = 4;
			} else {
				block();
			}
			break;
		}
	}

	public boolean done() {
		return ((step == 2 && bestSeller == null) || step == 4);
	}
}
