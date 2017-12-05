package group6;

import negotiator.issue.Value;

public class MyValueFrequency {
	private Value value;
	private Integer frequency;

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	@Override
	public String toString() {
		return "[value=" + value + ", frequency=" + frequency + "]";
	}

}
