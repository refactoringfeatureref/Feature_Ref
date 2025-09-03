package br.featureref.organicref.dataanalysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import br.featureref.organicref.optimization.algorithms.localsearch.CoolingSchedule;
import br.featureref.organicref.optimization.results.dtos.OptimizationResultDTO;
import br.featureref.organicref.optimization.results.dtos.SolutionDTO;
import br.featureref.organicref.optimization.results.dtos.context.ContextDTO;
import br.featureref.organicref.optimization.results.dtos.context.ContextImpactSummaryDTO;
import br.featureref.organicref.optimization.results.dtos.context.TypeInContextDTO;
import br.featureref.organicref.optimization.runners.AlgorithmTypeEnum;
import br.featureref.organicref.quality.metrics.TypeMetrics;

public class ReleaseResults
{
  public String releaseName;
  private AnalysisType analysisType;
  public final Map<UUID, OptimizationResultDTO> optimizationResults = new HashMap<>();
  public final Map<UUID, ContextImpactSummaryDTO> contextImpactSummaries = new HashMap<>();
  public Map<String, List<SolutionDTO>> nonDominatedPerAlgorithm = new HashMap<>();
  public Map<String, StrategyStats> strategyStatsMap = new HashMap<>();

  public ReleaseResults(String releaseName, AnalysisType analysisType) {
    this.releaseName = releaseName;
    this.analysisType = analysisType;
  }

  private List<OptimizationResultDTO> getResultsOfAlgorithm(AlgorithmTypeEnum algorithmType, CoolingSchedule coolingSchedule) {
    final List<OptimizationResultDTO> collected = optimizationResults.values()
        .stream()
        .filter(v -> algorithmType.name().toLowerCase().equals(v.algorithm.toLowerCase()) && v.population.size() > 0)
        .collect(Collectors.toList());

    if  (algorithmType.equals(AlgorithmTypeEnum.SA)) {
      return collected.stream()
          .filter(r -> {
            return r.population.get(0)
                .additionalInformation
                .get("schedule")
                .toString().toLowerCase()
                .equals(coolingSchedule.name().toLowerCase());
          }).collect(Collectors.toList());
    }
    return collected;
  }

  public Optional<ContextImpactSummaryDTO> getContextImpactOf(UUID executionId) {
    return Optional.ofNullable(contextImpactSummaries.get(executionId));
  }

  public void processData()
  {
    for(AlgorithmTypeEnum algoType : AlgorithmTypeEnum.values())
    {
      if (algoType.equals(AlgorithmTypeEnum.SA)) {
        processDataOf(algoType, CoolingSchedule.BOLTZ);
        processDataOf(algoType, CoolingSchedule.EXPONENTIAL);
      } else {
        processDataOf(algoType, null);
      }
    }
  }

  private void processDataOf(AlgorithmTypeEnum algoType, CoolingSchedule schedule)
  {
    StrategyStats stats = new StrategyStats();
    stats.strategyName = algoType.name() + (schedule != null ? schedule.name() : "");
    final List<OptimizationResultDTO> resultsOfAlgorithm = getResultsOfAlgorithm(algoType, schedule);

    if (resultsOfAlgorithm.size() == 0) {
      return;
    }

    final List<SolutionDTO> nonDominatedSolutions = getNonDominatedSolutions(resultsOfAlgorithm);
    nonDominatedPerAlgorithm.put(stats.strategyName, nonDominatedSolutions);

    final Set<UUID> nonDominatedSolutionsId = nonDominatedSolutions.stream()
        .map(s -> s.solutionId)
        .collect(Collectors.toSet());

//    System.out.println();
//    System.out.println(releaseName + ": " + stats.strategyName);

    for (OptimizationResultDTO dto : resultsOfAlgorithm)
    {
      stats.computingTimeStats.addValue(dto.computingTime);
      final Optional<ContextImpactSummaryDTO> contextImpactOpt = getContextImpactOf(dto.executionId);
      if (contextImpactOpt.isPresent()) {
        ContextImpactSummaryDTO contextSummary = contextImpactOpt.get();

        int numberOfConcernsBefore = 0;
        int numberOfSmellsBefore = 0;
        double lcomBefore = 0;
        double couplingintensityBefore = 0;
        double dispersedCouplingBefore = 0;
        for (TypeInContextDTO typeInContextDTO : contextSummary.beforeRefactoring.typesInContext) {
          numberOfConcernsBefore += typeInContextDTO.concerns.size();
          numberOfSmellsBefore += typeInContextDTO.symptoms.size();
          lcomBefore += typeInContextDTO.metrics.get(TypeMetrics.LCOM3);
          couplingintensityBefore  += typeInContextDTO.metrics.get(TypeMetrics.COUPLING_INTENSITY);
          dispersedCouplingBefore  += typeInContextDTO.metrics.get(TypeMetrics.COUPLING_DISPERSION);
        }

        lcomBefore = lcomBefore / contextSummary.beforeRefactoring.typesInContext.size();
        couplingintensityBefore = couplingintensityBefore / contextSummary.beforeRefactoring.typesInContext.size();
        dispersedCouplingBefore = dispersedCouplingBefore / contextSummary.beforeRefactoring.typesInContext.size();

        for (ContextDTO contextDTO : contextSummary.solutionsSummaries) {
          //We will evaluate only the best solutions of each algorithm
          if (!nonDominatedSolutionsId.contains(contextDTO.solutionId)) continue;

//          System.out.println("Original Project: ");
//          System.out.println("NumberOfConcerns: " + dto.projectBeforeRefactoring.meanNumConcerns);
//          System.out.println("LackOfCohesion: " + dto.projectBeforeRefactoring.meanLackOfCohesion);
//          System.out.println("MetricsIntensity: " + dto.projectBeforeRefactoring.meanMetricsIntensity);
//          System.out.println("Density: " + dto.projectBeforeRefactoring.meanDensity);
//          System.out.println();

          int numberOfConcernsAfter = 0;
          int numberOfSmellsAfter = 0;
          double lcomAfter = 0;
          double couplingintensityAfter = 0;
          double dispersedCouplingAfter = 0;
          for (TypeInContextDTO typeInContextDTO : contextDTO.typesInContext) {
            numberOfConcernsAfter += typeInContextDTO.concerns.size();
            numberOfSmellsAfter += typeInContextDTO.symptoms.size();
            lcomAfter += typeInContextDTO.metrics.get(TypeMetrics.LCOM3);
            couplingintensityAfter  += typeInContextDTO.metrics.get(TypeMetrics.COUPLING_INTENSITY);
            dispersedCouplingAfter  += typeInContextDTO.metrics.get(TypeMetrics.COUPLING_DISPERSION);
          }
          lcomAfter = lcomAfter / contextDTO.typesInContext.size();
          couplingintensityAfter = couplingintensityAfter / contextSummary.beforeRefactoring.typesInContext.size();
          dispersedCouplingAfter = dispersedCouplingAfter / contextSummary.beforeRefactoring.typesInContext.size();
          stats.smellNumberDiffStats.addValue(numberOfSmellsAfter - numberOfSmellsBefore);
          stats.concernNumberDiffStats.addValue(numberOfConcernsAfter - numberOfConcernsBefore);
          stats.lcomDiffStats.addValue(lcomAfter - lcomBefore);
          stats.couplingIntensity.addValue(couplingintensityAfter - couplingintensityBefore);
          stats.couplingDispersion.addValue(dispersedCouplingAfter - dispersedCouplingBefore);
        }
      }
    }

    for (SolutionDTO solution : nonDominatedSolutions)
    {
//      System.out.println("After Refactoring: ");
//      solution.objectives.entrySet().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

      stats.euclideanDistanceStats.addValue(solution.euclideanDistance);
      stats.numberRefactoringsStats.addValue(solution.numberOfRefactorings);
    }
    System.out.println();
    strategyStatsMap.put(stats.strategyName, stats);
  }

  private List<SolutionDTO> getNonDominatedSolutions(List<OptimizationResultDTO> solutions)
  {
    if (solutions.size() > 0)
    {
      var selector = new NonDominatedSolutionsSelector(solutions);
      final List<SolutionDTO> nonDominatedSolutions = selector.getNonDominatedSolutions();

      //long countLimit = Math.round(nonDominatedSolutions.size() * 0.25);
      long countLimit = nonDominatedSolutions.size();
//      long countLimit = 1;

      if (analysisType == AnalysisType.EUCLIDEAN_DISTANCE) {
        return nonDominatedSolutions.stream()
            .sorted(Comparator.comparingDouble(s -> s.euclideanDistance))
            .limit(countLimit)
            .collect(Collectors.toList());
      }

      if (analysisType == AnalysisType.SMALLER_REF_SEQUENCE) {
        return nonDominatedSolutions.stream()
            .sorted(Comparator.comparingInt(s -> s.numberOfRefactorings))
            .limit(countLimit)
            .collect(Collectors.toList());
      }

      if (analysisType == AnalysisType.SMALLER_SMELL_NUMBER) {
        return nonDominatedSolutions.stream()
            .sorted(Comparator.comparingDouble(s -> s.objectives.get("Density")))
            .limit(countLimit)
            .collect(Collectors.toList());
      }

      if (analysisType == AnalysisType.SMALLER_FEATURE_NUMBER) {
        return nonDominatedSolutions.stream()
            .sorted(Comparator.comparingDouble(s -> s.objectives.get("NumberOfConcerns")))
            .limit(countLimit)
            .collect(Collectors.toList());
      }

      if (analysisType == AnalysisType.SMALLER_LCOM) {
        return nonDominatedSolutions.stream()
            .sorted(Comparator.comparingDouble(s -> s.objectives.get("LackOfCohesion")))
            .limit(countLimit)
            .collect(Collectors.toList());
      }

      return nonDominatedSolutions;
    }
    return new ArrayList<>();
  }

  @Override
  public String toString()
  {
    return "ReleaseResults{" +
        "releaseName='" + releaseName + '\'' +
        ", strategyStatsMap=" + strategyStatsMap +
        '}';
  }

  public enum AnalysisType {
    EUCLIDEAN_DISTANCE,
    SMALLER_REF_SEQUENCE,
    SMALLER_SMELL_NUMBER,
    SMALLER_FEATURE_NUMBER,
    SMALLER_LCOM,
    NO_FILTER
  }
}
