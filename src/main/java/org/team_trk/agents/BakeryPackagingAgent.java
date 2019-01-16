package org.team_trk.agents;

import java.util.ArrayList;
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
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BakeryPackagingAgent extends BaseAgent {
	private static final long serialVersionUID = -5310054528477305012L;

	public final static Integer INFORM_COOLED_PRODUCT = 12341;

	/**
	 * Scheduler info (product orders)
	 */
	private List<Order> orders;

	private Map<String, CooledProduct> availableProducts;

	private Map<String, Integer> productsPerBox;

	private List<OutObject> out;

	private AID loadingBayAID;

	// Put agent initializations here
	protected void setup() {
		super.setup();
		// Register service in the yellow pages
//		DFAgentDescription dfd = new DFAgentDescription();
//		dfd.setName(getAID());
//		ServiceDescription sd = new ServiceDescription();
//		sd.setType("bakery-packaging");
//		sd.setName("JADE-bakery-packaging");
//		dfd.addServices(sd);
//		try {
//			DFService.register(this, dfd);
//		} catch (FIPAException fe) {
//			fe.printStackTrace();
//		}
		register("bakery-packaging", getAID().getName());

		availableProducts = new HashMap<>();
		String bakeryGuid = (String) getArguments()[0];
		productsPerBox = (Map<String, Integer>) getArguments()[1];
		orders = new ArrayList<>();
		out = new ArrayList<>();
		/*
		 * Arrays.asList(new OutObject("order-001", Arrays.asList(new Box("Bread", 10),
		 * new Box("Muffin", 2))), new OutObject("order-002", Arrays.asList(new
		 * Box("Bread", 6), new Box("Berliner", 1))), new OutObject("order-001",
		 * Arrays.asList(new Box("Bagel", 6))), new OutObject("order-002",
		 * Arrays.asList(new Box("Muffin", 8))), new OutObject("order-002",
		 * Arrays.asList(new Box("Donut", 8))), new OutObject("order-001",
		 * Arrays.asList(new Box("Berliner", 2))), new OutObject("order-002",
		 * Arrays.asList(new Box("Bagel", 6)))));
		 */

		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				boolean found = false;
				while (!found) {

					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd2 = new ServiceDescription();
					sd2.setType(bakeryGuid + "-loading-bay");
					template.addServices(sd2);
					try {
						Thread.sleep(3000);
						AID aid = null;
						aid = DFService.search(myAgent, template)[0].getName();
						loadingBayAID = aid;
						System.out.println("Packaging agent found loading bay!");
						found = true;
					} catch (FIPAException e) {
						System.err.println("Packaging agent cant find loading bay!");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		addBehaviour(new CyclicBehaviour() {

			private static final long serialVersionUID = -1612002656692984627L;

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
				ACLMessage msg = baseAgent.receive(mt);
				if (msg != null) {
					PropagatedOrders propagatedOrders = new Gson().fromJson(msg.getContent(), PropagatedOrders.class);
					boolean skip = false;
					for (Order o : propagatedOrders) {
						skip = false;
						for (Order known : orders) {
							if (known.getGuid().equals(o.getGuid())) {
								skip = true;
								break;
							}
						}
						if (!skip) {
							orders.add(o);
						}
					}
					orders.sort((Order a, Order b) -> {
						int diff = a.getDate().get("day") - b.getDate().get("day");
						return ((diff != 0) ? diff : a.getDate().get("hour") - b.getDate().get("hour"));
					});
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
			addBehaviour(new CyclicBehaviour() {

				private static final long serialVersionUID = -1424838066396490515L;

				private int step_counter = 0;

				@Override
				public void action() {
					switch (step_counter) {
					case 0:
						MessageTemplate mt = MessageTemplate.MatchPerformative(INFORM_COOLED_PRODUCT);
						ACLMessage msg = baseAgent.receive(mt);
						if (msg != null) {
							String content = msg.getContent(); // String created out of the ProductMessage-Object with
							System.out.println("received packaging content: " + content); // right
							// brothers individual class JsonConverter
							CooledProduct[] incoming = new Gson().fromJson(content, CooledProduct[].class);
							for (CooledProduct c : incoming) {
								if (availableProducts.get(c.getType()) == null) {
									availableProducts.put(c.getType(), new CooledProduct(c.getType(), 0));
								}
								availableProducts.get(c.getType()).addQuantity(c.getQuantity());
							}
						}
						step_counter++;
						break;
					case 1:
						for (Order o : orders) {
							OutObject orderOut = new OutObject(o.getGuid());
							Map<String, Integer> products = o.getProducts();
							for (String type : products.keySet()) {
								if (availableProducts.get(type) != null) {
									int available = availableProducts.get(type).getQuantity();
									int needed = products.get(type);
									if (needed <= available || available >= productsPerBox.get(type)) {
										int maxPerBox = productsPerBox.get(type);
										while (available >= needed) {
											Box box = new Box(type, needed > maxPerBox ? maxPerBox : needed);
											availableProducts.get(type).addQuantity(-1 * box.getQuantity());
											products.put(type, products.get(type) - box.getQuantity());
											orderOut.getBoxes().add(box);

											needed = products.get(type);
											available = availableProducts.get(type).getQuantity();
										}
									}
								}
							}
							if (!orderOut.getBoxes().isEmpty()) {
								out.add(orderOut);
							}
						}
						step_counter++;
						break;
					case 2:
						if (!out.isEmpty()) {
							// runs through case 2 until List is empty
							OutObject item = out.get(0);
							ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
							cfp.addReceiver(loadingBayAID);
							Gson gsonBuilder = new GsonBuilder().create();
							String jsonFromPojo = gsonBuilder.toJson(item);
							cfp.setContent(jsonFromPojo);
							cfp.setConversationId("packaged-orders");
							System.out.println(String.format("Sending package to loading bay: %s", cfp.getContent()));
							sendMessage(cfp);
							out.remove(0);
						} else {
							step_counter++;
						}
						break;
					default:
						step_counter = 0;
					}
				}
			});
			finished();
			addBehaviour(new WaitForAllowAction());
		}
	}

	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Packaging " + getAID().getName() + " terminating.");
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

}

/**
 * Object that is used to generate a json string the form the loading bay needs
 * it.
 * 
 * @author tim
 *
 */
class OutObject {
	private String OrderID;
	private List<Box> Boxes;

	public OutObject(String orderId) {
		this(orderId, new ArrayList<>());
	}

	public OutObject(String orderId, List<Box> boxes) {
		this.OrderID = orderId;
		this.Boxes = boxes;
	}

	public String getOrderId() {
		return OrderID;
	}

	public void setOrderId(String orderId) {
		this.OrderID = orderId;
	}

	public List<Box> getBoxes() {
		return Boxes;
	}

	public void setBoxes(List<Box> boxes) {
		this.Boxes = boxes;
	}

	@Override
	public String toString() {
		return String.format("[%s:%s]", OrderID, Boxes);
	}

}

/**
 * Logical box object that contains a certain amount of a given type of product.
 * 
 * @author tim
 *
 */
class Box {
	private static int id_counter = 0;

	private String ProductType;
	private int Quantity;
	private String BoxID;

	public Box(String type, int amount) {
		id_counter++;
		this.ProductType = type;
		this.Quantity = amount;
		this.BoxID = "" + id_counter;
	}

	public String getProductType() {
		return ProductType;
	}

	public void setProductType(String productType) {
		ProductType = productType;
	}

	public int getQuantity() {
		return Quantity;
	}

	public void setQuantity(int quantity) {
		Quantity = quantity;
	}

	public String getBoxID() {
		return BoxID;
	}

	@Override
	public String toString() {
		return String.format("[%s:%s:%s]", BoxID, ProductType, Quantity);
	}

}

/**
 * Class that is used to parse the incoming products from the cooling racks.
 * 
 * @author tmeule2s
 *
 */
class CooledProduct {

	private String type;
	private int quantity;

	public CooledProduct() {
		this("", 0);
	}

	public CooledProduct(String type, int quantity) {
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

/**
 * Needed info to orders told by the scheduler.
 * 
 * @author tim
 *
 */
class Order {

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

class PropagatedOrders extends ArrayList<Order> {

	private static final long serialVersionUID = -5367244431724163560L;

}