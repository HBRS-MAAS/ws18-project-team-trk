package org.team_trk.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BakeryPackagingAgent extends BaseAgent {
	private static final long serialVersionUID = -5310054528477305012L;

	/**
	 * Scheduler info (product orders)
	 */
	private List<Order> orders;

	private Map<String, CooledProduct> availableProducts;

	private Map<String, Integer> productsPerBox;

	// Put agent initializations here
	protected void setup() {
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
		productsPerBox = new HashMap<>();

		List<OutObject> out = new ArrayList<>();/*
												 * Arrays.asList(new OutObject("order-001", Arrays.asList(new
												 * Box("Bread", 10), new Box("Muffin", 2))), new OutObject("order-002",
												 * Arrays.asList(new Box("Bread", 6), new Box("Berliner", 1))), new
												 * OutObject("order-001", Arrays.asList(new Box("Bagel", 6))), new
												 * OutObject("order-002", Arrays.asList(new Box("Muffin", 8))), new
												 * OutObject("order-002", Arrays.asList(new Box("Donut", 8))), new
												 * OutObject("order-001", Arrays.asList(new Box("Berliner", 2))), new
												 * OutObject("order-002", Arrays.asList(new Box("Bagel", 6)))));
												 */

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("loading-bay");
		template.addServices(sd2);
		AID aid = null;
		try {
			aid = DFService.search(this, template)[0].getName();
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AID aid2 = aid;

		addBehaviour(new CyclicBehaviour() {

			private static final long serialVersionUID = -1424838066396490515L;

			private int step_counter = 0;

			@Override
			public void action() {
				if (!getAllowAction()) {
					return;
				}
				switch (step_counter) {
				case 0:
					MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
					ACLMessage msg = baseAgent.receive(mt);
					if (msg != null) {
						String content = msg.getContent(); // String created out of the ProductMessage-Object with right
															// brothers individual class JsonConverter
						CooledProduct[] incoming = new Gson().fromJson(content, CooledProduct[].class);
						for (CooledProduct c : incoming) {
							if (availableProducts.get(c.getType()) == null) {
								availableProducts.put(c.getType(), new CooledProduct(c.getType(), 0));
							}
							availableProducts.get(c.getType()).addQuantity(c.getQuantity());
						}
						step_counter++;
					}
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
						cfp.addReceiver(aid2);
						Gson gsonBuilder = new GsonBuilder().create();
						String jsonFromPojo = gsonBuilder.toJson(item);
						cfp.setContent(jsonFromPojo);
						cfp.setConversationId("packaged-orders");
						sendMessage(cfp);
						System.out.println(cfp);
						out.remove(0);
					} else {
						step_counter++;
					}
					break;
				default:
					// reset behaviour and wait for next incoming message
					step_counter = 0;
					block();
				}
				finished();

			}
		});

		// receive incoming orders
		addBehaviour(new CyclicBehaviour() {

			private static final long serialVersionUID = -1612002656692984627L;

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
				ACLMessage msg = baseAgent.receive(mt);
				if (msg != null) {
					System.out.println(msg.getContent());
				} else {
					block();
				}
			}
		});
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

}