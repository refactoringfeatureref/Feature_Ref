package br.featureref.organicref.quality.symptoms.detectors;

import static br.featureref.organicref.util.StatisticsUtil.*;

import java.util.ArrayList;
import java.util.List;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.quality.metrics.TypeMetrics;
import br.featureref.organicref.quality.symptoms.LazyClass;

public class LazyClassDetector
{
	public static List<LazyClass> detect(Project project) {
		List<LazyClass> result = new ArrayList<>();
		double percentile25 = calcFirstPercentile(project, TypeMetrics.NUMBER_OF_STATEMENTS);
		for (Type type : project.getAllTypes()) {
			LazyClass symptom = detect(type, percentile25);
			if (symptom != null) {
				result.add(symptom);
			}
		}
		
		return result;
	}

	private static double calcFirstPercentile(Project project, TypeMetrics typeMetric) {
		return getPercentil(25, getMetricValues(project.getAllTypes(), typeMetric));
	}

	private static LazyClass detect(Type type, double percentile) {
		int numFields = (int) type.getMetric(TypeMetrics.NUMBER_OF_FIELDS);
		int numMethods = (int) type.getMetric(TypeMetrics.NUMBER_OF_METHODS);
		int numStatements = (int) type.getMetric(TypeMetrics.NUMBER_OF_STATEMENTS);
		
		if (numStatements < percentile) {
			return new LazyClass(type, numFields, numMethods, numStatements);
		}
		
		return null;
	}
}
