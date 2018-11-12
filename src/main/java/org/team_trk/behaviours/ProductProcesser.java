package org.team_trk.behaviours;

import jade.core.behaviours.CyclicBehaviour;

public class ProductProcesser extends CyclicBehaviour {
	private static final long serialVersionUID = -3863996398471466048L;
	private Runnable process;

	public ProductProcesser(Runnable process) {
		this.process = process;
	}

	public void action() {
		process.run();
	}
}