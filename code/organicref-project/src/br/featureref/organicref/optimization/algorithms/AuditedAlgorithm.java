package br.featureref.organicref.optimization.algorithms;

import java.util.List;

import br.featureref.organicref.optimization.results.dtos.PartialProgressSummaryDTO;

public interface AuditedAlgorithm
{
  List<PartialProgressSummaryDTO> getPartialProgressList();
}
