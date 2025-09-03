package br.featureref.organicref.optimization.operators.mutation;

import org.uma.jmetal.operator.mutation.MutationOperator;

import br.featureref.organicref.optimization.algorithms.localsearch.SolutionNeighborhoodExplorer;
import br.featureref.organicref.optimization.solution.RefactoringSolution;

public class RefactoringSequenceMutationOperator implements MutationOperator<RefactoringSolution> {

	private static final long serialVersionUID = -2769508595441734234L;
	private static final double MUTATION_PROB = 0.2;
	private SolutionNeighborhoodExplorer neighborhoodExplorer;

	public RefactoringSequenceMutationOperator() {
		neighborhoodExplorer = new SolutionNeighborhoodExplorer();
	}
	
	@Override
	public RefactoringSolution execute(RefactoringSolution source) {
		final RefactoringSolution newSolution = neighborhoodExplorer.getNeighbor(source);
		newSolution.setGeneration(source.getGeneration() + 1);
		return newSolution;
	}

	@Override
	public double getMutationProbability() {
		return MUTATION_PROB;
	}
}
