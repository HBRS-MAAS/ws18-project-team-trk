//package org.team_trk.agents;
//
//import java.util.HashMap;
//
//import org.team_trk.gui.MessageQueueGUI;
//
//import jade.core.Agent;
//import jade.core.behaviours.CyclicBehaviour;
//import jade.core.behaviours.TickerBehaviour;
//import jade.domain.DFService;
//import jade.domain.FIPAException;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;
//import jade.lang.acl.ACLMessage;
//import jade.lang.acl.MessageTemplate;
//import jade.lang.acl.UnreadableException;
//
//public class MessageQueueAgent extends Agent {
//	private static final long serialVersionUID = -5310054528477305012L;
//
//	private MessageQueueGUI gui;
//
//	private AgentQueueMap agentQueueMap;
//
//	// Put agent initializations here
//	@SuppressWarnings("serial")
//	protected void setup() {
//		agentQueueMap = new AgentQueueMap();
//
//		// Register service in the yellow pages
//		DFAgentDescription dfd = new DFAgentDescription();
//		dfd.setName(getAID());
//		ServiceDescription sd = new ServiceDescription();
//		sd.setType("message-queue");
//		sd.setName("JADE-message-queue");
//		dfd.addServices(sd);
//		try {
//			DFService.register(this, dfd);
//		} catch (FIPAException fe) {
//			fe.printStackTrace();
//		}
//
//		addBehaviour(new CyclicBehaviour() {
//
//			@Override
//			public void action() {
//				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
//				ACLMessage msg = myAgent.receive(mt);
//				if (msg != null) {
//					Boolean in;
//					try {
//						in = (Boolean) msg.getContentObject();
//						if (in) {
//							agentQueueMap.increaseAmount(msg.getSender().getName());
//						} else {
//							agentQueueMap.decreaseAmount(msg.getSender().getName());
//						}
//						if (gui != null)
//							gui.update(agentQueueMap);
//					} catch (UnreadableException e) {
//						e.printStackTrace();
//					}
//				} else {
//					block();
//				}
//			}
//
//		});
//		addBehaviour(new TickerBehaviour(this, 5000) {
//
//			@Override
//			protected void onTick() {
//				gui = MessageQueueGUI.getInstance();
//				if (gui != null) {
//					stop();
//				}
//			}
//		});
//	}
//
//	protected void takeDown() {
//		// Printout a dismissal message
//		System.out.println("Bakery-agent " + getAID().getName() + " terminating.");
//		// Deregister from the yellow pages
//		try {
//			DFService.deregister(this);
//
//			if (gui != null) {
//				gui.stop();
//			}
//		} catch (FIPAException fe) {
//			fe.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
////	private DFAgentDescription[] listAgents() throws FIPAException {
////		return DFService.search(this, new DFAgentDescription());
////	}
//
////	class EventPrintList extends ArrayList<AgentQueueMap> {
////
////		private static final long serialVersionUID = 328647187803597475L;
////
////		@Override
////		public boolean remove(Object a) {
////			return processEvent(() -> {
////				return super.remove(a);
////			});
////		}
////
////		@Override
////		public boolean add(AgentQueueMap a) {
////			return processEvent(() -> {
////				return super.add(a);
////			});
////		}
////
////		private boolean processEvent(Callable<Boolean> r) {
////			boolean ret;
////			try {
////				ret = r.call();
////			} catch (Exception e) {
////				return false;
////			}
////			if (ret) {
////				System.out.println("MessageQueue states: " + this);
////			}
////			return ret;
////		}
////
////	}
//
//}
//
//class AgentQueueMap extends HashMap<String, Integer> {
//
//	private static final long serialVersionUID = -8289958649180527179L;
//
//	public void increaseAmount(String key) {
//		if (this.get(key) == null) {
//			this.put(key, 1);
//		} else {
//			this.put(key, this.get(key) + 1);
//		}
//	}
//
//	public void decreaseAmount(String key) {
//		if (this.get(key) != null) {
//			if (this.get(key) == 1) {
//				this.put(key, null);
//			} else {
//				this.put(key, this.get(key) - 1);
//			}
//		}
//	}
//
//}