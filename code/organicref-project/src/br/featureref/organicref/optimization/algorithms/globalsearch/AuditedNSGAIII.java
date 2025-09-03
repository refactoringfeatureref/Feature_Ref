package br.featureref.organicref.optimization.algorithms.globalsearch;

import static br.featureref.organicref.util.StatisticsUtil.calcEuclideanDistance;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;

import br.featureref.organicref.optimization.algorithms.AuditedAlgorithm;
import br.featureref.organicref.optimization.solution.RefactoringSolution;

import br.featureref.organicref.optimization.results.dtos.PartialProgressSummaryDTO;

public class AuditedNSGAIII<S extends RefactoringSolution> extends NSGAIII<S> implements AuditedAlgorithm
{
  private final int snapshotCollectionInterval;
  private int snapshotIntervalCounter = 0;

  private final List<PartialProgressSummaryDTO> partialProgressList = new ArrayList<>();

  public AuditedNSGAIII(final AuditedNSGAIIIBuilder<S> builder)
  {
    super(builder);
    this.snapshotCollectionInterval = builder.getSnapshotCollectionInterval();
  }

  @Override
  protected void updateProgress() {
    super.updateProgress();

    if (snapshotIntervalCounter == snapshotCollectionInterval) {
      saveNewSnapshot();
      snapshotIntervalCounter = 0;
    } else {
      snapshotIntervalCounter++;
    }
  }

  private void saveNewSnapshot()
  {
    final List<S> partialResult = getResult();
    if (partialResult.size() > 0)
    {
      final S solution = partialResult.stream()
          .sorted((s1, s2) -> Double.compare(calcEuclideanDistance(s1.getObjectives()),calcEuclideanDistance(s2.getObjectives())))
          .findFirst()
          .get();

      PartialProgressSummaryDTO dto = new PartialProgressSummaryDTO();
      dto.currentIteration = iterations;
      dto.objectives = solution.getObjectives();
      dto.euclideanDistance = calcEuclideanDistance(solution.getObjectives());
      partialProgressList.add(dto);
    }
  }

  @Override
  public List<PartialProgressSummaryDTO> getPartialProgressList()
  {
    return partialProgressList;
  }
}
