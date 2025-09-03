package br.featureref.organicref.clusterization.concernbased;

import br.featureref.organicref.model.entities.Type;

public class ConcernBasedTypeElementsClusterDetectorFactory {
	
	public ConcernBasedTypeElementsClustersDetector createFor(Type type) {
		return new ConcernBasedTypeElementsClustersDetector(type);
	}
}