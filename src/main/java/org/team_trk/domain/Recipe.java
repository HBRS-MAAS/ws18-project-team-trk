package org.team_trk.domain;

import java.util.List;

public class Recipe {
	private int coolingRate;
	private int bakingTemp;

	private List<Step> steps;

	public int getCoolingRate() {
		return coolingRate;
	}

	public void setCoolingRate(int coolingRate) {
		this.coolingRate = coolingRate;
	}

	public int getBakingTemp() {
		return bakingTemp;
	}

	public void setBakingTemp(int bakingTemp) {
		this.bakingTemp = bakingTemp;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	class Step {
		private String action;
		private int duration;

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

	}
}
