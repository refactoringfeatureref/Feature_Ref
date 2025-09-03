package br.featureref.organicref.optimization.results.dtos;

import java.util.List;
import java.util.UUID;

public class OptimizationResultDTO
{
  public UUID executionId;
  public long computingTime;
  public int maxEvaluations;
  public int initialPopulationSize;
  public int finalPopulationSize;
  public String algorithm;
  public ProjectSummaryDTO projectBeforeRefactoring;
  public List<SolutionDTO> population;

  public static final String RESULTS_FILE_SUFFIX = "resulting_population.json";
}
