package br.featureref.organicref.clusterization.jaccard;

import java.util.List;

import br.featureref.organicref.model.entities.Element;

public class EntitySet<T extends Element> {
	
	private T element;
	private List<String> associatedStrings;
	private int index;

	public EntitySet(T element, List<String> associatedStrings, int index) {
		this.element = element;
		this.associatedStrings = associatedStrings;
		this.index = index;
	}
	
	public T getElement() {
		return this.element;
	}
	
	public List<String> getAssociatedStrings() {
		return this.associatedStrings;
	}

	public int getIndex() {
		return index;
	}
}
