package org.team_trk.domain;

public class Product {
	private String guid;
	private Batch batch;
	private Recipe recipe;
	private Packaging packaging;
	private double salesPrice;
	private double productionCost;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public Packaging getPackaging() {
		return packaging;
	}

	public void setPackaging(Packaging packaging) {
		this.packaging = packaging;
	}

	public double getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(double salesPrice) {
		this.salesPrice = salesPrice;
	}

	public double getProductionCost() {
		return productionCost;
	}

	public void setProductionCost(double productionCost) {
		this.productionCost = productionCost;
	}

	class Batch {
		private int breadsPerOven;

		public int getBreadsPerOven() {
			return breadsPerOven;
		}

		public void setBreadsPerOven(int breadsPerOven) {
			this.breadsPerOven = breadsPerOven;
		}
	}
}
