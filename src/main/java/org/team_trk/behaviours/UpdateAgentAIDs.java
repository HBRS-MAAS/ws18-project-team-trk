/*
package org.team_trk.behaviours;

import java.util.Arrays;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class UpdateAgentAIDs extends TickerBehaviour {

	private static final long serialVersionUID = -147484173252708723L;

	private boolean expectResponse = false;

	private List<AID> agentAIDs;

	public UpdateAgentAIDs(Agent owner, List<AID> agentAIDs) {
		super(owner, 5000);
		this.agentAIDs = agentAIDs;
	}

	@Override
	protected void onTick() {
		// Update the list of seller agents
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery-prep-table");
//		sd.addProperties(new Property("location", getAgent().here()));
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			agentAIDs.clear();
			for (int i = 0; i < result.length; ++i) {
				if (getAgent().here().getID().equals(result[i].getName().getAllUserDefinedSlot().get("location"))) {
					agentAIDs.add(result[i].getName());
				}
				System.out.println(Arrays.deepToString(result[i].getName().getAddressesArray()));
				System.out.println(Arrays.deepToString(result[i].getName().getResolversArray()));
				System.out.println(result[i].getName().getAllUserDefinedSlot());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("prep tables: " + agentAIDs);
	}
}
*/