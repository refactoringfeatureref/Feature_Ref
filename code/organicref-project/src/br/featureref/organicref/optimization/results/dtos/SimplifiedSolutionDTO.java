package br.featureref.organicref.optimization.results.dtos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimplifiedSolutionDTO
{
  public UUID solutionId;
  public Map<String, Double> objectives = new HashMap<>();
  public double euclideanDistance;
}
