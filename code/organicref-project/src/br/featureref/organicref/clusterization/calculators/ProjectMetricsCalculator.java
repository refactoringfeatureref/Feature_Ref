package br.featureref.organicref.clusterization.calculators;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.quality.metrics.MethodMetrics;
import br.featureref.organicref.quality.metrics.TypeMetrics;

public class ProjectMetricsCalculator {
	
	public static void calculate(Project project) {
		
		for (Type type : project.getAllTypes()) {
			calculateForMethods(type);

			type.setMetric(TypeMetrics.NUMBER_OF_FIELDS, NumberOfFieldsCalculator.getValue(type));

			type.setMetric(TypeMetrics.NUMBER_OF_METHODS, NumberOfMethodsCalculator.getValue(type));

			type.setMetric(TypeMetrics.NUMBER_OF_STATEMENTS, NumberOfStatementsCalculator.getValue(type));

			type.setMetric(TypeMetrics.CYCLOMATIC_COMPLEXITY, CyclomaticComplexityCalculator.getValue(type));

			type.setMetric(TypeMetrics.COUPLING_INTENSITY, CouplingIntensityCalculator.getValue(type, project));

			type.setMetric(TypeMetrics.COUPLING_DISPERSION, CouplingDispersionCalculator.getValue(type, project));

			type.setMetric(TypeMetrics.NUMBER_OF_CLIENTS, NumberOfClientsCalculator.getValue(type, project));

			type.setMetric(TypeMetrics.LCOM3, LCOM3Calculator.getValue(type));
		}
	}

	private static void calculateForMethods(final Type type)
	{
		for (Method method : type.getMethods()) {
			method.setMetric(MethodMetrics.NUMBER_OF_STATEMENTS, NumberOfStatementsCalculator.getValue(method));
			method.setMetric(MethodMetrics.CYCLOMATIC_COMPLEXITY, CyclomaticComplexityCalculator.getValue(method));
		}
	}
}
