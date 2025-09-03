package br.featureref.organicref.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.MathArrays;

import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.quality.metrics.TypeMetrics;

public class StatisticsUtil
{
  public static List<Double> getMetricValues(Collection<Type> types, TypeMetrics typeMetric)
  {
    return types
        .stream()
        .map(t -> t.getMetric(typeMetric))
        .collect(Collectors.toList());
  }

  public static double getPercentil(double percentil, Collection<Double> elements)
  {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    elements.stream().forEach(e -> stats.addValue(e));
    return stats.getPercentile(percentil);
  }

  public static double getMedian(Collection<Double> elements)
  {
    return getPercentil(50, elements);
  }

  public static double calcEuclideanDistance(final double[] objectives)
  {
    double[] ideal = new double[objectives.length];
    Arrays.fill(ideal, 0.0);
    return MathArrays.distance(ideal, objectives);
  }
}
