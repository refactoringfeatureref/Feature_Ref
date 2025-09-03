package br.featureref.organicref.optimization.fitness;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.optimization.solution.RefactoringSolution;

public interface ObjectiveFunction {

	double evaluate(RefactoringSolution solution);

	DescriptiveStatistics evaluate(Project project);

	String getObjectiveName();

	static ObjectiveFunction[] getAllObjectiveFunctions() {
		ObjectiveFunction[] functions = new ObjectiveFunction[] {
				new NumberOfConcernsObjective(),
				new NumberOfRefactoringsObjective(),
				new DensityObjective(),
				new MetricsIntensityObjective(),
				new LackOfCohesionObjective()
		};
		
		return functions;
	}

	default double normalize(double max, double mean) {
		return mean / max;
	}
}
