package br.featureref.organicref.optimization.problem;

import java.util.HashSet;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.sequenceproblem.SequenceProblem;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.optimization.fitness.ObjectiveFunction;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.generators.RandomSolutionGenerator;

public class RefactoringProblem extends AbstractGenericProblem<RefactoringSolution> implements SequenceProblem<RefactoringSolution> {

	private static final long serialVersionUID = 9048265996254436519L;
	private ObjectiveFunction[] objectiveFunctions;
	private RandomSolutionGenerator generator;
	private HashSet<String> generatedKeys;

	public RefactoringProblem(Project project, ObjectiveFunction[] objectiveFunctions) {
		this.objectiveFunctions = objectiveFunctions;
		this.setNumberOfObjectives(objectiveFunctions.length);
		this.generator = new RandomSolutionGenerator(project, objectiveFunctions);
		this.generatedKeys = new HashSet<>();
	}
	
	@Override
	public void evaluate(RefactoringSolution solution) {
		solution.measureObjectives();
	}

	@Override
	public RefactoringSolution createSolution() {
		//TODO improve this method
		String key = null;
		RefactoringSolution refactoringSolution = null;
		do
		{
			refactoringSolution = generator.run();
			key = refactoringSolution.getDescription();
			if (!generatedKeys.contains(key)) {
				generatedKeys.add(key);
				return refactoringSolution;
			}
		}
		while (generatedKeys.contains(key));

		return null;
	}

	@Override
	public int getLength() {
		return getNumberOfVariables();
	}
}
