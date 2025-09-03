package br.featureref.organicref.optimization.results.dtos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SolutionDTO
{
  public UUID solutionId;
  public Map<String, Double> objectives = new HashMap<>();
  public double euclideanDistance;
  public int generation;
  public Integer numberOfRefactorings;
  public Map<String, Object> additionalInformation;
  public List<RefactoringSequenceDTO> sequences;
}
