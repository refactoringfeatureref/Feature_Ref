package br.featureref.organicref.clusterization.jaccard;

import br.featureref.organicref.model.entities.Type;

public class JaccardClassElementsClustersDetectorFactory {
	
	public JaccardClassElementsClustersDetector createFor(Type type) {
		return new JaccardClassElementsClustersDetector(type);
	}
}
