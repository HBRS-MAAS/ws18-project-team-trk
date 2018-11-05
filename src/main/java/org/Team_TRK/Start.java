package org.Team_TRK;

import java.util.ArrayList;
import java.util.List;

import org.team_trk.agents.BakeryCustomerAgent;
import org.team_trk.agents.BakeryProcessingAgent;
import org.team_trk.domain.BreadOrder;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Start {
	public static void main(String[] args) throws StaleProxyException {
//    	List<String> agents = new Vector<>();
//    	agents.add("test:org.Team_TRK.agents.DummyAgent");
//
//    	List<String> cmd = new Vector<>();
//    	cmd.add("-agents");
//    	StringBuilder sb = new StringBuilder();
//    	for (String a : agents) {
//    		sb.append(a);
//    		sb.append(";");
//    	}
//    	cmd.add(sb.toString());
//        jade.Boot.main(cmd.toArray(new String[cmd.size()]));

		// Get a hold on JADE runtime
		jade.core.Runtime rt = jade.core.Runtime.instance();

		// Exit the JVM when there are no more containers around
		rt.setCloseVM(true);
		rt.invokeOnTermination(() -> {
			System.out.println("End of Simulation!");
		});
		System.out.print("runtime created\n");

		// Create a default profile
		Profile profile = new ProfileImpl(null, 1200, null);
		System.out.print("profile created\n");

		// rt.startUp(profile);

		System.out.println("Launching a whole in-process platform..." + profile);
		jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);

		System.out.println("containers created");
		System.out.println("Launching the rma agent on the main container ...");
		AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
		rma.start();

		List<String> sellerNames = new ArrayList<>();
		sellerNames.add("Test-Bakery");

		for (String name : sellerNames) {
			AgentController seller = mainContainer.createNewAgent(name, BakeryProcessingAgent.class.getName(),
					new Object[] {});
			seller.start();
		}

		List<String> customerNames = new ArrayList<>();
		customerNames.add("Test-Customer");

		for (String name : customerNames) {
			AgentController customer = mainContainer.createNewAgent(name, BakeryCustomerAgent.class.getName(),
					new Object[] { new BreadOrder() });
			customer.start();
		}

	}
}
