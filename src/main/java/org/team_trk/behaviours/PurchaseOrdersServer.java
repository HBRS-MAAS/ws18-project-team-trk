package org.team_trk.behaviours;

import org.json.JSONObject;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PurchaseOrdersServer extends CyclicBehaviour {

	private static final long serialVersionUID = 7938556330444711767L;

//	private Hashtable<String, Book> catalogue;

	public PurchaseOrdersServer(/* Hashtable<String, Book> catalogue */) {
//		this.catalogue = catalogue;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
		ACLMessage msg = myAgent.receive(mt);
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
			myAgent.send(reply);
		} else {
			block();
		}

	}

}
