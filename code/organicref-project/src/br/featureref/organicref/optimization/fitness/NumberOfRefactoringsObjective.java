package br.featureref.organicref.optimization.fitness;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.optimization.solution.RefactoringSolution;

public class NumberOfRefactoringsObjective implements ObjectiveFunction {

	@Override
	public double evaluate(RefactoringSolution solution) {
		DescriptiveStatistics stats = new DescriptiveStatistics();

		solution.getVariables()
				.stream()
				.forEach(v-> stats.addValue(v.getRefactorings().size()));

		return stats.getSum();
	}

	@Override
	public DescriptiveStatistics evaluate(final Project project)
	{
		return new DescriptiveStatistics();
	}

	@Override
	public String getObjectiveName()
	{
		return "NumberOfRefactorings";
	}
}
