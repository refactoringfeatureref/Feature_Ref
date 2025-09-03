package br.featureref.organicref.quality.symptoms.detectors;

import static br.featureref.organicref.util.StatisticsUtil.getMetricValues;
import static br.featureref.organicref.util.StatisticsUtil.getPercentil;

import java.util.ArrayList;
import java.util.List;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.quality.metrics.ThresholdTypes;
import br.featureref.organicref.quality.metrics.TypeMetrics;
import br.featureref.organicref.quality.symptoms.GodClass;

public class GodClassDetector
{
  public static List<GodClass> detect(Project project)
  {
    List<GodClass> result = new ArrayList<>();

    Double complexityThreshold = project.getProjectThreshold(ThresholdTypes.CYCLOMATIC_COMPLEXITY_MEDIAN);
    if (complexityThreshold == null) {
      complexityThreshold = calcLastPercentile(project, TypeMetrics.CYCLOMATIC_COMPLEXITY, 50);
      project.setProjectThreshold(ThresholdTypes.CYCLOMATIC_COMPLEXITY_MEDIAN, complexityThreshold);
    }

    Double numStatementsThreshold = project.getProjectThreshold(ThresholdTypes.NUMBER_STATEMENTS_MEDIAN);
    if (numStatementsThreshold == null) {
      numStatementsThreshold = calcLastPercentile(project, TypeMetrics.NUMBER_OF_STATEMENTS, 50);
      project.setProjectThreshold(ThresholdTypes.NUMBER_STATEMENTS_MEDIAN, numStatementsThreshold);
    }

    Double numClientsThreshold = project.getProjectThreshold(ThresholdTypes.NUMBER_CLIENTS_75_PERCENTIL);
    if (numClientsThreshold == null) {
      numClientsThreshold = calcLastPercentile(project, TypeMetrics.NUMBER_OF_CLIENTS, 75);
      project.setProjectThreshold(ThresholdTypes.NUMBER_CLIENTS_75_PERCENTIL, numClientsThreshold);
    }

    for (Type type : project.getAllTypes())
    {
      GodClass symptom = detect(type, complexityThreshold, numStatementsThreshold, numClientsThreshold);
      if (symptom != null)
      {
        result.add(symptom);
      }
    }
    return result;
  }

  private static GodClass detect(final Type type, final Double complexityThreshold, final Double numStatementsThreshold,
      final Double numClientsThreshold)
  {
    double typeComplexity = type.getMetric(TypeMetrics.CYCLOMATIC_COMPLEXITY);
    double typeSize = type.getMetric(TypeMetrics.NUMBER_OF_STATEMENTS);
    double typeClients = type.getMetric(TypeMetrics.NUMBER_OF_CLIENTS);

    if (typeComplexity >= complexityThreshold && typeSize > numStatementsThreshold
    && typeClients > numClientsThreshold) {
      return new GodClass(type, typeClients, typeSize, typeComplexity);
    }

    return null;
  }

  private static double calcLastPercentile(Project project, TypeMetrics typeMetric, final int percentil)
  {
    return getPercentil(percentil, getMetricValues(project.getAllTypes(), typeMetric));
  }
}
