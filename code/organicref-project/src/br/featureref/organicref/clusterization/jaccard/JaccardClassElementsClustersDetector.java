package br.featureref.organicref.clusterization.jaccard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.util.jaccard.JaccardIndexCalculator;

public class JaccardClassElementsClustersDetector {
	
	private static final Double MAX_DIST_THRESHOLD = 0.8;
	private static final int MIN_NUM_CLUSTERS = 2;
	Map<Integer, JaccardCluster> clusters;
	private EntitySetBuilder esBuilder;
	private Double[][] clustersMinDistances;
	private int currentMinI;
	private int currentMinJ;
	private Double currentMinDistance;
	
	public JaccardClassElementsClustersDetector(Type type) {
		this.clusters = new HashMap<>();
		this.esBuilder = new EntitySetBuilder(type);
	}
	
	public void detectClusters() {
		createClustersForInitialEntitySets();
		calculateJaccardDistances();
		mergeClusters();
	}

	private void createClustersForInitialEntitySets() {
		esBuilder.build();
		for (EntitySet<? extends Element> es : esBuilder.getEntitySets()) {
			JaccardCluster cluster = new JaccardCluster(es);
			clusters.put(es.getIndex(), cluster);
		}
	}

	private void calculateJaccardDistances() {
		List<EntitySet<? extends Element>> entitySets = esBuilder.getEntitySets();
		int numESets = entitySets.size();
		
		clustersMinDistances = new Double[numESets][numESets];
		for (int i = 0; i < clustersMinDistances.length; i++) {
			for (int j = 0; j < clustersMinDistances.length; j++) {
				clustersMinDistances[i][j] = null;
			}
		}
		
		for (int i = 0; i < numESets; i++) {
			for (int j = 0; j < numESets; j++) {
				
				EntitySet<? extends Element> entitySetI = entitySets.get(i);
				EntitySet<? extends Element> entitySetJ = entitySets.get(j);
				
				if (clustersMinDistances[entitySetJ.getIndex()][entitySetI.getIndex()] != null) {
					//We don't need to calculated if we've already calculated
					clustersMinDistances[entitySetI.getIndex()][entitySetJ.getIndex()] = clustersMinDistances[entitySetJ.getIndex()][entitySetI.getIndex()];
				} else {
					if (entitySetI.getIndex() == entitySetJ.getIndex()) {
						clustersMinDistances[entitySetI.getIndex()][entitySetJ.getIndex()] = 0.0d;
					} else {
						List<String> stringsI = entitySetI.getAssociatedStrings();
						List<String> stringsJ = entitySetJ.getAssociatedStrings();
						
						clustersMinDistances[entitySetI.getIndex()][entitySetJ.getIndex()] = JaccardIndexCalculator.distance(stringsI, stringsJ);
					}
				}
			}
		}
	}
	
	private void mergeClusters() {
		updateCurrentMinDistance();
		while (currentMinDistance <= MAX_DIST_THRESHOLD && clusters.size() > MIN_NUM_CLUSTERS) {
			JaccardCluster clusterI = clusters.get(currentMinI);
			JaccardCluster clusterJ = clusters.get(currentMinJ);
			
			merge(clusterI, clusterJ);
			
			updateCurrentMinDistance();
		}
	}

	private void merge(JaccardCluster clusterI, JaccardCluster clusterJ) {
		int jIndex = clusterJ.getIndex();
		int iIndex = clusterI.getIndex();

		clusterI.mergeWith(clusterJ);
		clusters.remove(jIndex);
		
		for (int x = 0; x < clustersMinDistances.length; x++) {
			if (x == iIndex || x == jIndex) continue;
			
			Double distanceI = clustersMinDistances[iIndex][x];
			Double distanceJ = clustersMinDistances[jIndex][x];
			if (distanceI != null && distanceJ != null) {
				double min = Double.min(distanceI, distanceJ);
				clustersMinDistances[iIndex][x] = min;
				clustersMinDistances[x][iIndex] = min;
			} else {
				clustersMinDistances[iIndex][x] = null;
				clustersMinDistances[x][iIndex] = null;
				clustersMinDistances[jIndex][x] = null;
				clustersMinDistances[x][jIndex] = null;
			}
		}
		
		for (int x = 0; x < clustersMinDistances.length; x++) {
			clustersMinDistances[x][jIndex] = null;
			clustersMinDistances[jIndex][x] = null;
		}
	}

	private void updateCurrentMinDistance() {
		currentMinI = -1;
		currentMinJ = -1;
		currentMinDistance = Double.MAX_VALUE;
		for (int i = 0; i < clustersMinDistances.length; i++) {
			for (int j = i+1; j < clustersMinDistances.length; j++) {
				if (clustersMinDistances[i][j] != null && clustersMinDistances[i][j] < currentMinDistance) {
					currentMinDistance = clustersMinDistances[i][j];
					currentMinI = i;
					currentMinJ = j;
				}
			}
		}
	}
	
	public Collection<JaccardCluster> getClusters() {
		return this.clusters.values();
	}

	public Collection<JaccardCluster> getSmallerClusters() {
		if (getClusters().size() == 0) {
			return new ArrayList<>();
		}

		List<JaccardCluster> sortedClusters = new ArrayList<>(getClusters());
		
		Collections.sort(sortedClusters, (o1, o2) -> {
			if (o1.size() > o2.size())
				return -1;
			if (o1.size() < o2.size())
				return 1;
			return 0;
		});
		
		return sortedClusters.subList(1, sortedClusters.size());
	}
}
