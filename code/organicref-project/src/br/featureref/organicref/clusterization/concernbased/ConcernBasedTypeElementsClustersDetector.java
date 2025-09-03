package br.featureref.organicref.clusterization.concernbased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.featureref.organicref.model.entities.Concern;
import br.featureref.organicref.model.entities.ElementConcern;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;

public class ConcernBasedTypeElementsClustersDetector {

	private Type type;
	private Map<Concern, ConcernBasedClassElementsCluster> clusters;

	ConcernBasedTypeElementsClustersDetector(Type type) {
		this.type = type;
		this.clusters = new HashMap<>();
	}

	public void findClusters() {
		for (Method method : type.getMethods()) {
			if (method.getElementConcerns().size() > 0) {
				ElementConcern elementConcern = method.getElementConcerns().first();
				Concern concern = elementConcern.getConcern();

				ConcernBasedClassElementsCluster cluster = getCluster(concern);

				cluster.addMethod(method);
			}
		}
	}

	private ConcernBasedClassElementsCluster getCluster(Concern concern) {
		ConcernBasedClassElementsCluster cluster = clusters.get(concern);
		if (cluster == null) {
			cluster = new ConcernBasedClassElementsCluster(concern);
			clusters.put(concern, cluster);
		}
		return cluster;
	}

	public List<ConcernBasedClassElementsCluster> getSmallerClusters() {
		List<ConcernBasedClassElementsCluster> sorted = new ArrayList<>(clusters.values());

		if (sorted.size() <= 1) {
			return new ArrayList<>();
		}

		sorted.sort((c1, c2) -> c1.getSize().compareTo(c2.getSize()));
		return sorted.subList(0, sorted.size() - 1);
	}
}
