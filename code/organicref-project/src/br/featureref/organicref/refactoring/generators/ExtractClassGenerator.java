package br.featureref.organicref.refactoring.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.featureref.organicref.clusterization.jaccard.JaccardClassElementsClustersDetector;
import br.featureref.organicref.clusterization.jaccard.JaccardClassElementsClustersDetectorFactory;
import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.optimization.solution.variables.ExtractOperationSequence;
import br.featureref.organicref.refactoring.operations.ExtractClass;

public class ExtractClassGenerator implements RefactoringGenerator<ExtractOperationSequence> {
	
	private static final int MIN_NUM_EXT_ELEMENTS = 2;
	private JaccardClassElementsClustersDetectorFactory detectorFactory;

	public ExtractClassGenerator(JaccardClassElementsClustersDetectorFactory detectorFactory) {
		this.detectorFactory = detectorFactory;
	}
	
	public Optional<ExtractOperationSequence> generate(Type type) {
		JaccardClassElementsClustersDetector detector = detectorFactory.createFor(type);
		detector.detectClusters();
		List<Element> elements = detector.getSmallerClusters()
												.stream()
												.map(c -> c.getElements())
												.flatMap(List::stream)
												.collect(Collectors.toList());
		
		elements = filterExtractableElements(elements);
		
		if (containsAMethod(elements) && elements.size() > MIN_NUM_EXT_ELEMENTS) {
			return Optional.of(new ExtractOperationSequence(new ExtractClass(elements, type), type.getFullyQualifiedName()));
		}
		
		return Optional.empty();
	}

	private boolean containsAMethod(List<Element> elements) {
		for (Element element : elements) {
			if (element instanceof Method) {
				return true;
			}
		}
		return false;
	}

	private List<Element> filterExtractableElements(List<Element> elements) {
		List<Element> extractable = new ArrayList<>();
		
		for (Element element : elements) {
			if (element instanceof Method) {
				Method method = (Method) element;
				
				if (!method.isOverride() && !method.isConstructor()) {
					extractable.add(method);
				}
				
			} else {
				extractable.add(element);
			}
		}
		
		return extractable;
	}
}