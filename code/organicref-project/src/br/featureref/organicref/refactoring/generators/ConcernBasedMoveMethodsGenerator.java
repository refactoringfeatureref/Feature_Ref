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
import br.featureref.organicref.model.entities.Concern;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;
import br.featureref.organicref.optimization.solution.variables.MoveOperationsSequence;
import br.featureref.organicref.refactoring.operations.MoveField;
import br.featureref.organicref.refactoring.operations.MoveMethod;
import br.featureref.organicref.refactoring.operations.MoveOperation;
import br.featureref.organicref.util.ListUtil;

public class ConcernBasedMoveMethodsGenerator implements RefactoringGenerator<MoveOperationsSequence> {

	private ConcernBasedTypeElementsClusterDetectorFactory factory;
	private Project project;

	public ConcernBasedMoveMethodsGenerator(ConcernBasedTypeElementsClusterDetectorFactory factory, Project project) {
		this.factory = factory;
		this.project = project;
	}

	public Optional<MoveOperationsSequence> generate(Type type) {
		ConcernBasedTypeElementsClustersDetector detector = factory.createFor(type);

		detector.findClusters();

		List<ConcernBasedClassElementsCluster> clusters = detector.getSmallerClusters();

		if (clusters.size() <= 1) {
			return Optional.empty();
		}

		List<MoveOperation> moves = new ArrayList<>();
		for (ConcernBasedClassElementsCluster cluster : clusters) {
			final Set<Method> methodsToMove = findMethodsToMove(cluster);
			final Concern concern = cluster.getPredominantConcern();
			final List<Type> targetTypes = project.getTypesOfConcern(concern)
					.stream()
					.filter(t -> !t.equals(type))
					.collect(Collectors.toList());
			if (targetTypes.size() > 0)
			{
				final Set<Field> fieldsToMove = findFieldsToMove(type, methodsToMove);
				final Type element = ListUtil.getAndRemoveRandomElementFrom(targetTypes);
				List<String> otherCandidates = targetTypes.stream().map(t -> t.getFullyQualifiedName()).collect(Collectors.toList());
				moves.addAll(methodsToMove.stream()
						.map(m -> new MoveMethod(m.getFullyQualifiedNameWithParams(), type.getFullyQualifiedName(),
								element.getFullyQualifiedName(), otherCandidates))
						.collect(Collectors.toList()));

				moves.addAll(fieldsToMove.stream()
						.map(f -> new MoveField(f.getFullyQualifiedName(), type.getFullyQualifiedName(),
								element.getFullyQualifiedName()))
						.collect(Collectors.toList()));
			}
		}

		if (moves.size() > 0) {
			return Optional.of(new MoveOperationsSequence(moves, type.getFullyQualifiedName()));
		}

		return Optional.empty();
	}

	private Set<Field> findFieldsToMove(Type type, Set<Method> methodsToExtract) {
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
			//Only move if the field is not used by not moved methods
			if (boundExtracted > 0 && boundNotExtracted == 0) {
				fieldsToExtract.add(field);
			}
		}
		
		return fieldsToExtract;
	}

	private Set<Method> findMethodsToMove(ConcernBasedClassElementsCluster cluster) {
			return cluster.getMethods()
							.stream()
							.filter(m -> !m.isConstructor())
							.collect(Collectors.toSet());
	}
}
