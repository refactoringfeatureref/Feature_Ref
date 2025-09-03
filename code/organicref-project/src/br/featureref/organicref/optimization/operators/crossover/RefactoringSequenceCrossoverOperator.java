package br.featureref.organicref.optimization.operators.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.util.checking.Check;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class RefactoringSequenceCrossoverOperator implements CrossoverOperator<RefactoringSolution> {
	
	private static final long serialVersionUID = -747461392756966738L;
	private static final int NUM_REQUIRED_PARENTS = 2;
	private static final int NUM_GENERATED_CHILDREN = 2;
	private static final double CROSSOVER_PROBABILITY = 0.8;
	
	@Override
	public List<RefactoringSolution> execute(List<RefactoringSolution> source) {
	    Check.isNotNull(source);
	    Check.that(source.size() == 2, "There must be two parents instead of " + source.size());
		
		RefactoringSolution firstParent = source.get(0);
		RefactoringSolution secondParent = source.get(1);
		
		return doCrossover(firstParent, secondParent);
	}

	private List<RefactoringSolution> doCrossover(RefactoringSolution firstParent, RefactoringSolution secondParent) {
		Map<String, List<RefactoringSequence>> firstTypeToRefactorings = createTypeToRefactoringsMap(firstParent.getVariables());
		Map<String, List<RefactoringSequence>> secondTypeToRefactorings = createTypeToRefactoringsMap(secondParent.getVariables());
		
		Set<String> refactoredTypesNames = new HashSet<>(firstTypeToRefactorings.keySet());
		refactoredTypesNames.addAll(secondTypeToRefactorings.keySet());
		
		List<RefactoringSequence> girlRefactorings = new ArrayList<>();
		List<RefactoringSequence> boyRefactorings = new ArrayList<>();
		
		for (String typeName : refactoredTypesNames) {
			int randomOpt = ThreadLocalRandom.current().nextInt(0, 2);
			List<RefactoringSequence> girlSelected;
			List<RefactoringSequence> boySelected;
			if (randomOpt == 0) {
				girlSelected = firstTypeToRefactorings.get(typeName);
				boySelected = secondTypeToRefactorings.get(typeName);
			} else {
				girlSelected = secondTypeToRefactorings.get(typeName);
				boySelected = firstTypeToRefactorings.get(typeName);
			}
			if (girlSelected != null) {
				girlRefactorings.addAll(girlSelected);
			}
			if (boySelected != null) {
				boyRefactorings.addAll(boySelected);
			}
		}

		int newGenerator = 1 + Math.max(firstParent.getGeneration(), secondParent.getGeneration());
		
		Project originalProject = (Project) firstParent.getAttribute(RefactoringSolution.ORIGINAL_PROJECT);
		int numberOfObjectives = firstParent.getNumberOfObjectives();
		RefactoringSolution girlSolution = new RefactoringSolution(girlRefactorings.size(), numberOfObjectives);
		girlSolution.setOriginalProject(originalProject);
		for (int i = 0; i < girlRefactorings.size(); i++) {
			RefactoringSequence refactoring = girlRefactorings.get(i).copy();
			girlSolution.setVariable(i, refactoring);
		}
		girlSolution.setGeneration(newGenerator);
		
		RefactoringSolution boySolution = new RefactoringSolution(boyRefactorings.size(), numberOfObjectives);
		boySolution.setOriginalProject(originalProject);
		for (int i = 0; i < boyRefactorings.size(); i++) {
			RefactoringSequence refactoring = boyRefactorings.get(i).copy();
			boySolution.setVariable(i, refactoring);
		}
		boySolution.setGeneration(newGenerator);
		
		return Arrays.asList(girlSolution, boySolution);
	}

	private Map<String, List<RefactoringSequence>> createTypeToRefactoringsMap(List<RefactoringSequence<Refactoring>> parentRefactorings) {
		Map<String, List<RefactoringSequence>> typeToRefactorings = new HashMap<>();
		for (RefactoringSequence refactoring : parentRefactorings) {
			String typeQualifiedName = refactoring.getOriginalTypeQualifiedName();
			List<RefactoringSequence> refsOfType = typeToRefactorings.get(typeQualifiedName);
			if (refsOfType == null) {
				refsOfType = new ArrayList<>();
				typeToRefactorings.put(typeQualifiedName, refsOfType);
			}
			refsOfType.add(refactoring);
		}
		return typeToRefactorings;
	}

	@Override
	public double getCrossoverProbability() {
		return CROSSOVER_PROBABILITY;
	}

	@Override
	public int getNumberOfRequiredParents() {
		return NUM_REQUIRED_PARENTS;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return NUM_GENERATED_CHILDREN;
	}

}
