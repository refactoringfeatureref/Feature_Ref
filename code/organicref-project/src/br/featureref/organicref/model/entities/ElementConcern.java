package br.featureref.organicref.model.entities;

import java.util.Objects;

public class ElementConcern implements Comparable<ElementConcern> {

	private Concern concern;
	private Double probability;
	private Element parentElement;

	public ElementConcern(Concern concern, Double probability) {
		this.concern = concern;
		this.probability = probability;
	}

	public Concern getConcern() {
		return concern;
	}

	public Double getProbability() {
		return probability;
	}

	@Override
	public int hashCode() {
		return Objects.hash(concern);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementConcern other = (ElementConcern) obj;
		return Objects.equals(concern, other.concern);
	}

	@Override
	public int compareTo(ElementConcern o) {
		int result = this.probability.compareTo(o.probability);
		if (result == 0) {
			result = Integer.compare(this.concern.getTopicId(), o.concern.getTopicId());
		}
		return result;
	}

	public Element getParentElement() {
		return parentElement;
	}

	public void setParentElement(Element parentElement) {
		this.parentElement = parentElement;
	}
	
	@Override
	public String toString() {
		return "Probability: " + this.getProbability() + " - " + concern.toString();
	}
}
