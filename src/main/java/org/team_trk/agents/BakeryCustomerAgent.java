package org.team_trk.agents;

import java.util.ArrayList;
import java.util.List;

import org.team_trk.behaviours.RequestPerformer;
import org.team_trk.domain.BreadOrder;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class BakeryCustomerAgent extends BaseAgent {

	private static int instance_counter = 0;

	private List<BreadOrder> breadOrders;

	private String name;

	private AID[] bakeries;

	public BakeryCustomerAgent() {
		instance_counter++;
	}

	@SuppressWarnings("unchecked")
	protected void setup() {
		// Printout a welcome message
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			name = (String) args[0];
			breadOrders = (List<BreadOrder>) args[1];
//			System.out.println("Trying to buy " + new JSONObject(breadOrder));
		} else {
			// Make the agent terminate immediately
			System.out.println("No breadOrder specified");
			doDelete();
		}

//		addBehaviour(new UpdateAgentAIDs(this, new ArrayList<>()));

		// Add a TickerBehaviour that schedules a request to seller agents every minute
		addBehaviour(new TickerBehaviour(this, 10000) {
			List<Behaviour> behaviours = new ArrayList<>();

			protected void onTick() {
				if (behaviours.size() > 0) {
					for (Behaviour b : behaviours) {
						if (!b.done()) {
							return;
						}
					}
					System.out.println("Agent " + myAgent.getName() + " got his order.");
					instance_counter--;
					if (instance_counter == 0) {
						ACLMessage shutdownMessage = new ACLMessage(ACLMessage.REQUEST);
						Codec codec = new SLCodec();
						myAgent.getContentManager().registerLanguage(codec);
						myAgent.getContentManager().registerOntology(JADEManagementOntology.getInstance());
						shutdownMessage.addReceiver(myAgent.getAMS());
						shutdownMessage.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
						shutdownMessage.setOntology(JADEManagementOntology.getInstance().getName());
						try {
							myAgent.getContentManager().fillContent(shutdownMessage,
									new Action(myAgent.getAID(), new ShutdownPlatform()));
							myAgent.send(shutdownMessage);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					myAgent.doDelete();
				}

				// Update the list of seller agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("bakery");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					bakeries = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						bakeries[i] = result[i].getName();
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				for (BreadOrder breadOrder : breadOrders) {
					RequestPerformer behav = new RequestPerformer(breadOrder, bakeries);
					behaviours.add(behav);
					addBehaviour(behav);
				}
			}
		});
	}

	protected void takeDown() {
		// Deregister from the yellow pages
//		try {
//			DFService.deregister(this);
//		} catch (FIPAException fe) {
//			fe.printStackTrace();
//		}
	}

}
