package br.featureref.organicref.quality.symptoms.detectors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import br.featureref.organicref.model.entities.Concern;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.quality.symptoms.TypeConcernDispersion;

public class TypeConcernDispersionDetector
{
	public static List<TypeConcernDispersion> detect(Project project) {
		List<TypeConcernDispersion> symptoms = new ArrayList<>();

		DescriptiveStatistics stats = new DescriptiveStatistics();

		var concernClusters = getDispersedConcernClusters(project);
		concernClusters.values()
				.stream()
				.filter(s -> s.size() > 0)
				.forEach(s -> stats.addValue(s.size()));

		final double medianDispersion = stats.getPercentile(50);

		for (var entry : concernClusters.entrySet()) {
			if (entry.getValue().size() > medianDispersion) {
				entry.getValue().stream().forEach(v -> {
					symptoms.add(new TypeConcernDispersion(v, entry.getKey()));
				});
			}
		}

		return symptoms;
	}
	
	private static Map<Concern, HashSet<Type>> getDispersedConcernClusters(Project project) {
		var map = project.getConcerns()
				.stream()
				.collect(Collectors.toMap(Function.identity(), v -> new HashSet<Type>()));

		for (Type type : project.getAllTypes())
		{
			if (type.getElementConcerns().size() > 1)
			{
				type.getElementConcerns()
						.stream()
						.skip(1) //We should skip the predominant concern
						.forEach(ec ->
						{
							map.get(ec.getConcern()).add(type);
						});
			}
		}
		return map;
	}
}
