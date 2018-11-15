package org.team_trk.agents;

import java.util.List;

import org.team_trk.behaviours.OfferRequestsServer;
import org.team_trk.behaviours.PurchaseOrdersServer;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BakeryProcessingAgent extends BaseAgent {
	private static final long serialVersionUID = -5310054528477305012L;

	private List<AID> prepTables;

	// Put agent initializations here

	protected void setup() {
		// Register service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery");
		sd.setName("JADE-bakery-processing");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

//		if (content != null) {
//			addBehaviour(new TickerBehaviour(this, 10000) {
//
//				protected void onTick() {
//					// Update the list of prep tables
//					QueryAgentsOnLocation ca = new QueryAgentsOnLocation();
//					ca.setLocation(getAgent().here()); // here is the information about you ontainer
//					Action actExpr = new Action(myAgent.getAMS(), ca);
//
//					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
//					request.addReceiver(myAgent.getAMS());
//					request.setOntology(JADEManagementOntology.getInstance().getName());
//					request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
//					request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
//					myAgent.getContentManager().fillContent(request, actExpr);
//					myAgent.send(request);
//					try {
//						DFAgentDescription[] result = DFService.search(myAgent, template);
//						prepTables = new ArrayList<>();
//						for (int i = 0; i < result.length; ++i) {
//							prepTables.add(result[i].getName());
//						}
//					} catch (FIPAException fe) {
//						fe.printStackTrace();
//					}
//				}
//			});
//		}

		// Add the behaviour serving requests for offer from buyer agents
		addBehaviour(new OfferRequestsServer(null));
		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer(null));
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