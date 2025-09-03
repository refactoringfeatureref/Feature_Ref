package br.featureref.organicref.clusterization.jaccard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Element;

public class JaccardCluster {
	
	private List<EntitySet<? extends Element>> entitySets;
	private int index;
	
	public JaccardCluster(EntitySet<? extends Element> firstEntitySet) {
		this.entitySets = new ArrayList<>();
		this.entitySets.add(firstEntitySet);
		this.index = firstEntitySet.getIndex();
	}
	
	public void mergeWith(JaccardCluster other) {
		this.entitySets.addAll(other.getEntitySets());
	}

	public Collection<? extends EntitySet<? extends Element>> getEntitySets() {
		return this.entitySets;
	}

	public int getIndex() {
		return index;
	}

	public int size() {
		return entitySets.size();
	}
	
	public List<Element> getElements() {
		return entitySets.stream().map(es -> es.getElement()).collect(Collectors.toList());
	}
}
