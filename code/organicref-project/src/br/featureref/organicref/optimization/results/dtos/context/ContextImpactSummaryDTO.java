package br.featureref.organicref.optimization.results.dtos.context;

import java.util.List;
import java.util.UUID;

public class ContextImpactSummaryDTO
{
  public UUID executionId;
  public ContextDTO beforeRefactoring;
  public List<ContextDTO> solutionsSummaries;

  public static final String CONTEXT_FILE_SUFFIX = "context_summary.json";
}
