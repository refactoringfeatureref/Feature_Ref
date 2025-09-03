package br.featureref.organicref.dataanalysis;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.point.PointSolution;

import br.featureref.organicref.optimization.results.dtos.OptimizationResultDTO;
import br.featureref.organicref.optimization.results.dtos.SolutionDTO;

public class NonDominatedSolutionsSelector
{
  public static final String DTO = "dto";
  private List<OptimizationResultDTO> optimizationResults;

  public NonDominatedSolutionsSelector(List<OptimizationResultDTO> optimizationResults)
  {
    this.optimizationResults = optimizationResults;
  }

  public List<SolutionDTO> getNonDominatedSolutions()
  {
    var all = optimizationResults.stream()
        .flatMap(r -> r.population.stream())
        .map(this::convertToPointSolution)
        .collect(Collectors.toList());

    return SolutionListUtils.getNonDominatedSolutions(all)
        .stream()
        .map(s -> (SolutionDTO) s.getAttribute(DTO))
        .collect(Collectors.toList());
  }

  private PointSolution convertToPointSolution(SolutionDTO solutionDTO) {
    Double[] objectives = solutionDTO.objectives.values().toArray(new Double[0]);
    PointSolution pointSolution = new PointSolution(objectives.length);
    for (int i = 0; i < objectives.length; i++) {
      pointSolution.setObjective(i, objectives[i]);
    }
    pointSolution.setAttribute(DTO, solutionDTO);
    return pointSolution;
  }
}
