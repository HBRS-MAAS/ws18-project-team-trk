package org.team_trk.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class BakeryPackagingAgent extends BaseAgent {
	private static final long serialVersionUID = -5310054528477305012L;
	private List<Object> orders;

	// Put agent initializations here
	protected void setup() {
		// Register service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery-packaging");
		sd.setName("JADE-bakery-packaging");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		List<?> in = new ArrayList<>();// TODO in typ bestimmen
		List<OutObject> out = new ArrayList<>(
				Arrays.asList(new OutObject("order-001", Arrays.asList(new Box("Bread", 10), new Box("Muffin", 2))),
						new OutObject("order-002", Arrays.asList(new Box("Bread", 6), new Box("Berliner", 1))),
						new OutObject("order-001", Arrays.asList(new Box("Bagel", 6))),
						new OutObject("order-002", Arrays.asList(new Box("Muffin", 8))),
						new OutObject("order-002", Arrays.asList(new Box("Donut", 8))),
						new OutObject("order-001", Arrays.asList(new Box("Berliner", 2))),
						new OutObject("order-002", Arrays.asList(new Box("Bagel", 6)))));

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("order-aggregator");
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
				switch (step_counter) {
				case 0:// neue order annehmen (nachgucken wie die aussieht)
					break;
				case 1:// in überprüfen und ggf boxen in out packen
					break;
				case 2:// send all boxes that contain all items of a type for an order
					if (!out.isEmpty()) {
						// runs through case 2 until List is empty
						OutObject item = out.get(0);
						ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
						cfp.addReceiver(aid2);
						Gson gsonBuilder = new GsonBuilder().create();
						String jsonFromPojo = gsonBuilder.toJson(item);
						cfp.setContent(jsonFromPojo);
						cfp.setConversationId("packaged-orders");
						myAgent.send(cfp);
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

class OutObject {
	private String OrderID;
	private List<Box> Boxes;

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

//TODO class für eingehende sachen