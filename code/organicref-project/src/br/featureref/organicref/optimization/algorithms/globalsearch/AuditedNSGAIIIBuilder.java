package br.featureref.organicref.optimization.algorithms.globalsearch;

import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.problem.Problem;

import br.featureref.organicref.optimization.solution.RefactoringSolution;

/** Builder class */
public class AuditedNSGAIIIBuilder<S extends RefactoringSolution> extends NSGAIIIBuilder<S>
{
  private int snapshotCollectionInterval = 30;

  /** Builder constructor */
  public AuditedNSGAIIIBuilder(Problem<S> problem) {
    super(problem);
  }

  public AuditedNSGAIIIBuilder setSnapshotCollectionInterval(int interval) {
    this.snapshotCollectionInterval = interval;
    return this;
  }

  public int getSnapshotCollectionInterval() {
    return this.snapshotCollectionInterval;
  }

  @Override
  public AuditedNSGAIII<S> build() {
    return new AuditedNSGAIII<S>(this) ;
  }
}
