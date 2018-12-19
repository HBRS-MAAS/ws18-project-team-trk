package org.team_trk;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.team_trk.agents.BakeryCustomerAgent;
import org.team_trk.agents.BakeryProcessingAgent;
import org.team_trk.agents.CustomerAgent;
import org.team_trk.agents.OrderProcessing;
import org.team_trk.agents.SchedulerAgent;
import org.team_trk.domain.BreadOrder;
import org.team_trk.domain.Product;

import com.google.gson.Gson;

import jade.content.frame.OrderedFrame;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Start {

	private static int instance_counter = 0;

	public static void main(String[] args) throws StaleProxyException, IOException, URISyntaxException {
		String scenarioPath = "";
		if (args != null && args.length > 0) {
			scenarioPath = args[0];
		} else {
//			PrintStream out = System.out;
//			OutputStream voidStream = new OutputStream() {
//				@Override
//				public void write(int b) throws IOException {
//				}
//			};
//			System.setOut(new PrintStream(voidStream));
//			String[] list = new File("project/src/main/resources/config/").list();
//			try {
//				for (String s : list) {
//					System.err.println("------------------------------------------ running " + s
//							+ " ------------------------------------------");
//					main(new String[] { "project/src/main/resources/config/" + s });
//					while (instance_counter > 0) {
//						System.out.println(instance_counter);
//					}
//					System.err.println("------------------------------------------ finished " + s
//							+ " ------------------------------------------");
//					System.err.println();
//				}
			scenarioPath = "src/main/resources/config/small";
//			} finally {
//				System.setOut(out);
//			}
//			System.err.println("finished all scenarios of: " + Arrays.toString(list));
//			return;
		}
		instance_counter++;
		System.out.println(String.format("Scenario is: %s", scenarioPath));

		// Get a hold on JADE runtime
		jade.core.Runtime rt = jade.core.Runtime.instance();

		// Exit the JVM when there are no more containers around
		rt.setCloseVM(!(args.length > 0));
		rt.invokeOnTermination(() -> {
			System.out.println("End of Simulation!");
			instance_counter--;
		});
		System.out.print("runtime created\n");

		// Create a default profile
		String ip = null;// "10.0.0.6";
		int port = 1200;// 1099;
		Profile profile = new ProfileImpl(ip, port, null);
		System.out.print("profile created\n");

		// rt.startUp(profile);

		System.out.println("Launching a whole in-process platform..." + profile);
		jade.wrapper.AgentContainer mainContainer = null;
		if (ip == null) {
			mainContainer = rt.createMainContainer(profile);
		} else {
			mainContainer = rt.createAgentContainer(profile);
		}
		System.out.println("containers created");
		System.out.println("Launching the rma agent on the main container ...");
		AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
		rma.start();

//		Thread t = new Thread() {
//			@Override
//			public void run() {
//				MessageQueueGUI.open(new String[0]);
//			}
//		};
//		t.start();

//		AgentController messageQueue = mainContainer.createNewAgent("MessageQueue", MessageQueueAgent.class.getName(),
//				new Object[0]);
//		messageQueue.start();

//		String prooferGUID = "bakery-proofer";
//		String coolingRacksGUID = "bakery-cooling-racks";
		String packagingGUID = "bakery-packaging";
//		String loadingBayGUID = "bakery-loading-bay";
//		String truckGUID = "truck";
//		String mailboxGUID = "customer-mailbox";
//
//		// start bakeries
//		List<BakeryObject> bakeries = loadConfigData(scenarioPath + "/bakeries.json", BakeryObjectList.class);
//		int port = 1200;
//		for (BakeryObject bObj : bakeries) {
//			port++;
//			// create a container for each bakery
		jade.wrapper.AgentContainer sideContainer = rt.createAgentContainer(new ProfileImpl(null, port, null));
//
//			List<AID> ovenGuids = new ArrayList<>();
//			List<AID> prepTableGuids = new ArrayList<>();
//			List<AID> kneadingMachineGuids = new ArrayList<>();
//			if (bObj.getEquipment() != null) {
//				// start equipment agents for the bakery
//				org.team_trk.BakeryObject.Equipment e = bObj.getEquipment();
//				for (Oven o : e.getOvens()) {
//					AgentController oven = sideContainer.createNewAgent(o.getGuid(), BakeryOvenAgent.class.getName(),
//							new Object[] { bObj.getGuid(), o.getCoolingRate(), o.getHeatingRate(),
//									coolingRacksGUID + "-" + port });
//					oven.start();
//					ovenGuids.add(new AID(o.getGuid(), true));
//				}
//				for (KneadingMachine km : e.getKneadingMachines()) {
//					AgentController oven = sideContainer.createNewAgent(km.getGuid(),
//							BakeryKneadingAgent.class.getName(),
//							new Object[] { bObj.getGuid(), prooferGUID + "-" + port });
//					oven.start();
//					kneadingMachineGuids.add(new AID(km.getGuid(), true));
//				}
//				for (DoughPrepTable dpt : e.getDoughPrepTables()) {
//					AgentController oven = sideContainer.createNewAgent(dpt.getGuid(),
//							BakeryDoughPrepTableAgent.class.getName(),
//							new Object[] { bObj.getGuid(), kneadingMachineGuids });
//					oven.start();
//					prepTableGuids.add(new AID(dpt.getGuid(), true));
//
//				}
//			}
//
//			// start rest of agents for the bakery
//			sideContainer.createNewAgent(prooferGUID + "-" + port, BakeryProoferAgent.class.getName(),
//					new Object[] { ovenGuids }).start();
//			sideContainer.createNewAgent(coolingRacksGUID + "-" + port, BakeryCoolingRacksAgent.class.getName(),
//					new Object[] { packagingGUID + "-" + port }).start();
//			sideContainer.createNewAgent(packagingGUID + "-" + port, BakeryPackagingAgent.class.getName(),
//					new Object[] { loadingBayGUID + "-" + port }).start();
//			sideContainer.createNewAgent(loadingBayGUID + "-" + port, BakeryLoadingBayAgent.class.getName(),
//					new Object[] { truckGUID + "-" + port }).start();
//			sideContainer.createNewAgent(truckGUID + "-" + port, TruckAgent.class.getName(), new Object[] {}).start();
//
//			// start processing agents of bakery
//		AgentController controller = sideContainer.createNewAgent(/* bObj.getGuid() */"bpagent",
//				BakeryProcessingAgent.class.getName(), /*
//														 * new Object[] { bObj.getName(), bObj.getProducts(), ovenGuids,
//														 * prepTableGuids, packagingGUID + "-" + port
//														 */new Object[0]);
//		controller.start();

		List<BakeryObject> bakeryObjects = loadConfigData(scenarioPath + "/bakeries.json", BakeryObjectList.class);

		for (BakeryObject o : bakeryObjects) {
			
			String bakeryObjectAsJsonString = new Gson().toJson(o);

			AgentController scheduler = sideContainer.createNewAgent("scheduler-" + o.getGuid().split("-")[1],
					SchedulerAgent.class.getName(), new Object[] { bakeryObjectAsJsonString, "{durationInDays:300}" });
			scheduler.start();


//			Object orderProcessingObject = new Object() {
//				String guid;
//				Product[] products = o.getProducts().toArray(new Product[o.getProducts().size()]);
//
//				public String getGuid() {
//					return guid;
//				}
//
//				public void setGuid(String guid) {
//					this.guid = guid;
//				}
//
//				public Product[] getProducts() {
//					return products;
//				}
//
//				public void setProducts(Product[] products) {
//					this.products = products;
//				}
//
//			};

			AgentController orderProcessing = sideContainer.createNewAgent(
					"OrderProcessing-" + o.getGuid().split("-")[1], OrderProcessing.class.getName(),
					new Object[] { bakeryObjectAsJsonString, "{durationInDays:300}" });
			orderProcessing.start();
		}

//		}

		// start clients
		List<ClientObject> clientObjects = loadConfigData(scenarioPath + "/clients.json", Clients.class);

		for (ClientObject cObj : clientObjects) {
//			AgentController customer = sideContainer.createNewAgent(cObj.getGuid(), CustomerAgent.class.getName(),
//					new Object[0]);
//			customer.start();
		}

	}

	private static <T> T loadConfigData(String fileName, Class<T> dataClass) throws IOException, URISyntaxException {
		URI uri = new File(fileName).toURI();
		byte[] bytes = Files.readAllBytes(Paths.get(uri));
		String json = new String(bytes);
		return (T) new Gson().fromJson(json, dataClass);
	}

}

class Clients extends ArrayList<ClientObject> {

	private static final long serialVersionUID = 15878813697085145L;

}

class ClientObject {
	private String guid;
	private int type;
	private String name;
	private Point2D.Double location;
	private List<BreadOrder> orders;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point2D.Double getLocation() {
		return location;
	}

	public void setLocation(Point2D.Double location) {
		this.location = location;
	}

	public List<BreadOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<BreadOrder> orders) {
		this.orders = orders;
	}

	class Location {
		private double x;
		private double y;

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

	}

	@Override
	public String toString() {
		return String.format("%s;%s;%s;%s;%s", guid, type, name, location, orders);
	}
}

class BakeryObjectList extends ArrayList<BakeryObject> {

	private static final long serialVersionUID = -7322926100500163504L;

}

class BakeryObject {
	private String guid;
	private String name;
	private Point2D.Double location;
	private List<Product> products;
	private Equipment equipment;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point2D.Double getLocation() {
		return location;
	}

	public void setLocation(Point2D.Double location) {
		this.location = location;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	class Equipment {
		List<Oven> ovens;
		List<DoughPrepTable> doughPrepTables;
		List<KneadingMachine> kneadingMachines;

		public List<Oven> getOvens() {
			return ovens;
		}

		public void setOvens(List<Oven> ovens) {
			this.ovens = ovens;
		}

		public List<DoughPrepTable> getDoughPrepTables() {
			return doughPrepTables;
		}

		public void setDoughPrepTables(List<DoughPrepTable> doughPrepTables) {
			this.doughPrepTables = doughPrepTables;
		}

		public List<KneadingMachine> getKneadingMachines() {
			return kneadingMachines;
		}

		public void setKneadingMachines(List<KneadingMachine> kneadingMachines) {
			this.kneadingMachines = kneadingMachines;
		}

		class Oven {
			String guid;
			int coolingRate;
			int heatingRate;

			public String getGuid() {
				return guid;
			}

			public void setGuid(String guid) {
				this.guid = guid;
			}

			public int getCoolingRate() {
				return coolingRate;
			}

			public void setCoolingRate(int coolingRate) {
				this.coolingRate = coolingRate;
			}

			public int getHeatingRate() {
				return heatingRate;
			}

			public void setHeatingRate(int heatingRate) {
				this.heatingRate = heatingRate;
			}

		}

		class DoughPrepTable {
			private String guid;

			public String getGuid() {
				return guid;
			}

			public void setGuid(String guid) {
				this.guid = guid;
			}

		}

		class KneadingMachine {
			private String guid;

			public String getGuid() {
				return guid;
			}

			public void setGuid(String guid) {
				this.guid = guid;
			}

		}
	}
}