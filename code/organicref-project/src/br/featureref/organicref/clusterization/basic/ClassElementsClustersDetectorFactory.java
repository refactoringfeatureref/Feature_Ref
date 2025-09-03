package br.featureref.organicref.clusterization.basic;

import br.featureref.organicref.model.entities.Type;

public class ClassElementsClustersDetectorFactory {
	
	public ClassElementsClustersDetector createFor(Type type) {
		return new ClassElementsClustersDetector(type);
	}
}
