package org.team_trk.behaviours;

import java.util.List;

import org.json.JSONObject;
import org.team_trk.agents.BaseAgentOld;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PurchaseOrdersServer extends CyclicBehaviour {

	private static final long serialVersionUID = 7938556330444711767L;

	private BaseAgentOld baseAgent;

//	private Hashtable<String, Book> catalogue;

	public PurchaseOrdersServer(List<Object> orderedBreads) {
	}

	@Override
	public void action() {
		if (baseAgent == null) {
			baseAgent = (BaseAgentOld) myAgent;
		}
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
		ACLMessage msg = baseAgent.receiveMessage(mt);
		if (msg != null) {
			String msgContent = msg.getContent();
			JSONObject order = new JSONObject(msgContent);
			System.out.println("Got accept proposal for order " + order);
//			Book book = catalogue.get(title);

			ACLMessage reply = msg.createReply();
//			if (book != null) {
			reply.setPerformative(ACLMessage.CONFIRM);
			reply.setContent("future breads");
//			} else {
//				reply.setPerformative(ACLMessage.DISCONFIRM);
//				reply.setContent("not-available");
//			}
			baseAgent.sendMessage(reply);
		} else {
			block();
		}

	}

}
