package br.featureref.organicref.dataanalysis;

import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class StrategyStats {
  public String strategyName;
  //SOLUTION
  public DescriptiveStatistics computingTimeStats = new DescriptiveStatistics();
  public DescriptiveStatistics euclideanDistanceStats = new DescriptiveStatistics();
  public DescriptiveStatistics numberRefactoringsStats = new DescriptiveStatistics();
  public DescriptiveStatistics densityStats = new DescriptiveStatistics();
  public DescriptiveStatistics qmeasuresStats = new DescriptiveStatistics();
  public DescriptiveStatistics lcomSolutionStats = new DescriptiveStatistics();
  public DescriptiveStatistics nfeatureStats = new DescriptiveStatistics();

  //CONTEXT
  public DescriptiveStatistics smellNumberDiffStats = new DescriptiveStatistics();
  public DescriptiveStatistics concernNumberDiffStats = new DescriptiveStatistics();
  public DescriptiveStatistics lcomDiffStats = new DescriptiveStatistics();
  public DescriptiveStatistics couplingIntensity = new DescriptiveStatistics();
  public DescriptiveStatistics couplingDispersion = new DescriptiveStatistics();

  @Override
  public String toString()
  {
    return "StrategyStats{" +
        "strategyName='" + strategyName + '\'' +
        ", computingTimeStats=" + computingTimeStats +
        ", euclideanDistanceStats=" + euclideanDistanceStats +
        ", numberRefactoringsStats=" + numberRefactoringsStats +
        ", smellNumberDiffStats=" + smellNumberDiffStats +
        ", concernNumberDiffStats=" + concernNumberDiffStats +
        ", cohesionDiffStats=" + lcomDiffStats +
        '}';
  }
}
