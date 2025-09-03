package br.featureref.organicref.optimization.fitness;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.optimization.solution.RefactoringSolution;

public class NumberOfConcernsObjective implements ObjectiveFunction {

	@Override
	public double evaluate(RefactoringSolution solution) {
		DescriptiveStatistics statsBefore = evaluate( (Project) solution.getAttribute(RefactoringSolution.ORIGINAL_PROJECT));
		DescriptiveStatistics statsAfter = evaluate( (Project) solution.getAttribute(RefactoringSolution.REFACTORED_PROJECT));
		return statsAfter.getSum() / statsBefore.getSum();
	}

	@Override
	public DescriptiveStatistics evaluate(final Project project)
	{
		DescriptiveStatistics stats = new DescriptiveStatistics();

		project.getAllTypes()
				.stream()
				.forEach(t -> stats.addValue(t.getElementConcerns().size()));

		return stats;
	}

	@Override
	public String getObjectiveName()
	{
		return "NumberOfConcerns";
	}
}
