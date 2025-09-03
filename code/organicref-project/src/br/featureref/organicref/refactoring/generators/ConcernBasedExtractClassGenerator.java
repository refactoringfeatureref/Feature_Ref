package br.featureref.organicref.refactoring.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.featureref.organicref.clusterization.concernbased.ConcernBasedClassElementsCluster;
import br.featureref.organicref.clusterization.concernbased.ConcernBasedTypeElementsClusterDetectorFactory;
import br.featureref.organicref.clusterization.concernbased.ConcernBasedTypeElementsClustersDetector;
import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;
import br.featureref.organicref.optimization.solution.variables.ExtractOperationSequence;
import br.featureref.organicref.refactoring.operations.ExtractClass;

public class ConcernBasedExtractClassGenerator implements RefactoringGenerator<ExtractOperationSequence> {

	private ConcernBasedTypeElementsClusterDetectorFactory factory;

	public ConcernBasedExtractClassGenerator(ConcernBasedTypeElementsClusterDetectorFactory factory) {
		this.factory = factory;
	}

	public Optional<ExtractOperationSequence> generate(Type type) {
		ConcernBasedTypeElementsClustersDetector detector = factory.createFor(type);

		detector.findClusters();

		List<ConcernBasedClassElementsCluster> clusters = detector.getSmallerClusters();

		if (clusters.size() <= 1) {
			return Optional.empty();
		}

		List<Element> elementsToExtract = new ArrayList<>();
		Set<Method> methodsToExtract = findMethodsToExtract(clusters);
		Set<Field> fieldsToExtract = findFieldsToExtract(type, methodsToExtract);
		elementsToExtract.addAll(methodsToExtract);
		elementsToExtract.addAll(fieldsToExtract);
		return Optional.of(new ExtractOperationSequence(new ExtractClass(elementsToExtract, type), type.getFullyQualifiedName()));
	}

	private Set<Field> findFieldsToExtract(Type type, Set<Method> methodsToExtract) {
		Set<Field> fieldsToExtract = new HashSet<>();
		
		List<Method> notExtractedMethods = type.getMethods()
												.stream()
												.filter(m -> !methodsToExtract.contains(m))
												.collect(Collectors.toList());
		
		List<Field> fields = type.getFields();
		MethodsFieldsRelationships relationships = type.getLocalMethodsFieldsRelationships();
		for (Field field : fields) {
			Set<Method> methodsThatUse = new HashSet<>(relationships.getMethodsThatUse(field));
			int boundExtracted = 0;
			int boundNotExtracted = 0;
			for (Method methodUsing : methodsThatUse) {
				if (methodsToExtract.contains(methodUsing)) {
					boundExtracted += 1;
				} else if (notExtractedMethods.contains(methodUsing)) {
					boundNotExtracted += 1;
				}
			}
			if (boundExtracted > boundNotExtracted) {
				fieldsToExtract.add(field);
			}
		}
		
		return fieldsToExtract;
	}

	private Set<Method> findMethodsToExtract(List<ConcernBasedClassElementsCluster> clusters) {
		Set<Method> methodsToExtract = new HashSet<>();
		for (ConcernBasedClassElementsCluster cluster : clusters) {
			methodsToExtract.addAll(cluster.getMethods()
							.stream()
							.filter(m -> !m.isConstructor())
							.collect(Collectors.toList()));
		}
		return methodsToExtract;
	}
}
