package br.featureref.organicref.optimization.results.dtos;

import java.util.List;
import java.util.UUID;

public class SimplifiedResultDTO
{
  public UUID executionId;
  public List<SimplifiedSolutionDTO> solutions;

  public static final String SIMPLIFIED_RESULTS_FILE_SUFFIX = "simplified_results.json";
}
