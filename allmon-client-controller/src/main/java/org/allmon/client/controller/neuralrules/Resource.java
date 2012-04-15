package org.allmon.client.controller.neuralrules;

public class Resource {
	
	final private String name;
//	final private boolean useOriginalValues;
	private double minValue, maxValue; // values are extended during training NN
	
	public Resource(String name) {
		this.name = name;
		this.minValue = 0;
		this.maxValue = 1;
	}
	
	public Resource(String name, double minValue, double maxValue) {
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	/**
	 * @param normalizedValue value between 0 and 1
	 * @return de-normalized value in scope of minValue and maxValue
	 */
	public double denormalize(double normalizedValue) {
		return minValue + normalizedValue * (maxValue - minValue);
	}

	public double normalize(double realValue) {
		if (realValue > maxValue) {
			throw new RuntimeException("This value (" + realValue + ") for resource " + name + 
					" is greater than declared maximum " + maxValue + " of the resource scope - normalization is impossible");
		}
		if (realValue < minValue) {
			throw new RuntimeException("This value (" + realValue + ") for resource " + name + 
					" is lower than declared minimum " + minValue + " of the resource scope - normalization is impossible");
		}
		return (realValue - minValue) / (maxValue - minValue);
	}
		
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name + "<normalized:" + minValue + "," + maxValue + ">";
	}

	public double getMinValue() {
		return minValue;
	}
	public double getMaxValue() {
		return maxValue;
	}
	
	public void setMinMaxValue(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
}