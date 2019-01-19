package org.team_trk.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.introspection.AddedContainer;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DummyPrePackagingAgent extends BaseAgent {

	private List<Order2> orders;

	private Map<String, Integer> orderedBreads;

	private AID packagingAID;

	protected void setup() {
		super.setup();
		register("bakery-pre-packaging", getAID().getName());

		orders = new ArrayList<>();
		orderedBreads = new HashMap<>();

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("bakery-packaging");
		template.addServices(sd2);
		try {
			packagingAID = DFService.search(this, template)[0].getName();
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		addBehaviour(new CyclicBehaviour() {

			private static final long serialVersionUID = -1612002656692984627L;

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
				ACLMessage msg = baseAgent.receive(mt);
				if (msg != null) {
					PropagatedOrders2 propagatedOrders = new Gson().fromJson(msg.getContent(), PropagatedOrders2.class);
					boolean skip = false;
					for (Order2 o : propagatedOrders) {
						skip = false;
						for (Order2 known : orders) {
							if (known.getGuid().equals(o.getGuid())) {
								skip = true;
								break;
							}
						}
						if (!skip) {
							orders.add(o);
							for (String p : o.getProducts().keySet()) {
								if (orderedBreads.get(p) == null) {
									orderedBreads.put(p, 0);
								}
								orderedBreads.put(p, orderedBreads.get(p) + o.getProducts().get(p));
							}
						}
					}
//					orders.sort((Order2 a, Order2 b) -> {
//						int diff = a.getDate().get("day") - b.getDate().get("day");
//						return ((diff != 0) ? diff : a.getDate().get("hour") - b.getDate().get("hour"));
//					});
				} else {
					block();
				}
			}
		});

		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				if (orders.size() > 0) {
					OutProduct out = new OutProduct();
					List<String> types = Arrays
							.asList(orderedBreads.keySet().toArray(new String[orderedBreads.keySet().size()]));
					if (types.size() > 0) {
						String type = types.get(((int) Math.random() * 100) % types.size());
						if (orderedBreads.get(type) > 0) {
							int amount = ((int) Math.random() * 100) % orderedBreads.get(type) + 1;

							orderedBreads.put(type, orderedBreads.get(type) - amount);
							if (orderedBreads.get(type) == 0) {
								orderedBreads.remove(type);
							}

							out.setType(type);
							out.setQuantity(amount);

							ACLMessage cfp = new ACLMessage(BakeryPackagingAgent.INFORM_COOLED_PRODUCT);
							cfp.addReceiver(packagingAID);
							Gson gsonBuilder = new GsonBuilder().create();
							String jsonFromPojo = gsonBuilder.toJson(out);
							cfp.setContent(String.format("[%s]", jsonFromPojo));
							System.out.println(String.format(getAID().getName()+": Sending content to packaging: %s", cfp.getContent()));
							cfp.setConversationId("packaged-orders");
							sendMessage(cfp);
						} else {
							orderedBreads.remove(type);
						}
					}
				} else {
					block();
				}
			}
		});

		addBehaviour(new WaitForAllowAction());
	}

	private class WaitForAllowAction extends OneShotBehaviour {

		private static final long serialVersionUID = 7649625418839495853L;

		@Override
		public void action() {
			if (!getAllowAction()) {
				addBehaviour(new WaitForAllowAction());
				return;
			}
			finished();
			addBehaviour(new WaitForAllowAction());
		}
	}
}

/**
 * Class that is used to parse the outgoing products to the packaging stage.
 * 
 * @author tmeule2s
 *
 */
class OutProduct {

	private String type;
	private int quantity;

	public OutProduct() {
		this("", 0);
	}

	public OutProduct(String type, int quantity) {
		this.type = type;
		this.quantity = quantity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}

}

class Order2 {

	private String guid;

	private Map<String, Integer> products;

	private Map<String, Integer> date;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Map<String, Integer> getProducts() {
		return products;
	}

	public void setProducts(Map<String, Integer> products) {
		this.products = products;
	}

	public Map<String, Integer> getDate() {
		return date;
	}

	public void setDate(Map<String, Integer> date) {
		this.date = date;
	}

//	@Override
//	public String toString() {
//		return String.format("%s -> day: %s, hour: %s | %s", guid, date.get("day") != null ? date.get("day") : "?",
//				date.get("hour") != null ? date.get("hour") : "?", products != null ? products : "{}");
//	}

}

class PropagatedOrders2 extends ArrayList<Order2> {

	private static final long serialVersionUID = -5367244431724163560L;

}
