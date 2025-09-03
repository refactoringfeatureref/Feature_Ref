package br.featureref.organicref.clusterization.basic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;

public class ClassElementsCluster {
	
	private Set<Element> elements;

	public ClassElementsCluster() {
		elements = new HashSet<>();
	}
	
	public void addElement(Element element) {
		this.elements.add(element);
	}
	
	public Set<Element> getElements() {
		return this.elements;
	}
	
	@Override
	public String toString() {
		return elements.toString();
	}

	public boolean containsElement(Element relatedElement) {
		return this.elements.contains(relatedElement);
	}
	
	public int size() {
		return elements.size();
	}

	public boolean hasFields() {
		for (Element element : elements) {
			if (element instanceof Field) {
				return true;
			}
		}
		return false;
	}

	public Collection<Method> getMethods() {
		return elements.stream()
						.filter(e -> e instanceof Method)
						.map(e -> (Method) e)
						.collect(Collectors.toList());
	}
}
