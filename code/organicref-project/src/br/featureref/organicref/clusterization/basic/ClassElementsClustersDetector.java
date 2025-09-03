package br.featureref.organicref.clusterization.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;

public class ClassElementsClustersDetector {

	private Type type;
	private List<ClassElementsCluster> clusters;
	private LinkedList<Field> availableFields;
	private LinkedList<Method> availableMethods;
	private MethodsFieldsRelationships methodsFieldsRelations;
	private MethodCallRelationships methodCalls;

	public ClassElementsClustersDetector(Type type) {
		this.type = type;
		this.methodsFieldsRelations = type.getLocalMethodsFieldsRelationships();
		this.methodCalls = type.getLocalMethodCallRelationships();
		this.clusters = new ArrayList<>();
	}

	public void findClusters() {
		buildAvailableElementsSets();
		//TODO optimize this algorithm
		while (hasAvailableElement()) {
			ClassElementsCluster cluster = new ClassElementsCluster();
			getClusters().add(cluster);

			LinkedList<Element> poolElements = new LinkedList<>();
			Element availableElement = getNextAvailableElement();
			poolElements.add(availableElement);
			
			while (poolElements.size() > 0) {
				Element currentElement = poolElements.poll(); 
				cluster.addElement(currentElement);
				removeFromAvailableElements(currentElement);
				
				List<? extends Element> relatedElements = getRelatedElements(currentElement);
				for (Element relatedElement : relatedElements) {
					//Avoid including elements that were already included in the cluster
					if (!cluster.containsElement(relatedElement)) {
						poolElements.add(relatedElement);
					}
				}
			}
		}
		sortClusters();
	}

	private void sortClusters() {
		Collections.sort(clusters, (o1, o2) -> {
			if (o1.size() > o2.size())
				return -1;
			if (o1.size() < o2.size())
				return 1;
			return 0;
		});
	}

	private void removeFromAvailableElements(Element currentElement) {
		if (currentElement instanceof Field) {
			availableFields.remove(currentElement);
		} else {
			availableMethods.remove(currentElement);
		}
	}

	private List<? extends Element> getRelatedElements(Element element) {
		if (element instanceof Field) {
			return methodsFieldsRelations.getMethodsThatUse((Field) element);
		} else {
			Method method = (Method) element;
			List<Field> fieldsUsedByMethod = methodsFieldsRelations.getFieldsUsedBy(method);
			Set<Method> methodsCalledByMethod = methodCalls.getMethodsCalledBy(method);
			Set<Method> methodsThatDependOnMethod = methodCalls.getMethodsThatDependOn(method);
			
			List<Element> allRelatedElements = new ArrayList<>();
			allRelatedElements.addAll(fieldsUsedByMethod);
			allRelatedElements.addAll(methodsCalledByMethod);
			allRelatedElements.addAll(methodsThatDependOnMethod);
			return allRelatedElements;
		}
	}

	private void buildAvailableElementsSets() {
		availableFields = new LinkedList<>();
		availableMethods = new LinkedList<>();
		availableFields.addAll(type.getFields());
		availableMethods.addAll(type.getMethods());
	}
	
	private boolean hasAvailableElement() {
		return availableFields.size() > 0 || availableMethods.size() > 0;
	}
	
	private Element getNextAvailableElement() {
		if (availableFields.size() > 0) {
			return availableFields.getFirst();
		}
		
		if (availableMethods.size() > 0) {
			return availableMethods.getFirst();
		}
		
		return null;
	}

	public List<ClassElementsCluster> getClusters() {
		return clusters;
	}

	public List<ClassElementsCluster> getSmallerClusters() {
		if (clusters.size() == 1)
			return clusters;
		
		List<ClassElementsCluster> smallerClusters = new ArrayList<>();
		for (int i = 1; i < clusters.size(); i++) {
			smallerClusters.add(clusters.get(i));
		}
		
		return smallerClusters;
	}
}
