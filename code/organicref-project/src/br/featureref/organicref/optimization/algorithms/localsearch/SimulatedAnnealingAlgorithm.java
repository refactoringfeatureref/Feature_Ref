package br.featureref.organicref.optimization.algorithms.localsearch;

import static br.featureref.organicref.util.StatisticsUtil.calcEuclideanDistance;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;

import br.featureref.organicref.optimization.algorithms.AuditedAlgorithm;
import br.featureref.organicref.optimization.results.dtos.PartialProgressSummaryDTO;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.util.StatisticsUtil;

public class SimulatedAnnealingAlgorithm implements Algorithm<List<RefactoringSolution>>, AuditedAlgorithm
{
  private int maxIterations;
  private double initialTemperature;
  private SolutionNeighborhoodExplorer mutator;
  private RefactoringSolution initialSolution;
  private List<RefactoringSolution> result = null;
  private int iterationBestSolution = 0;
  private CoolingSchedule coolingSchedule;
  private int snapshotCollectionInterval;
  private List<PartialProgressSummaryDTO> partialProgressList = null;

  public SimulatedAnnealingAlgorithm(RefactoringSolution initialSolution)
  {
    this(1000, 100, initialSolution, CoolingSchedule.EXPONENTIAL, 30);
  }

  public SimulatedAnnealingAlgorithm(int maxIterations, double initialTemperature,
      final RefactoringSolution initialSolution, CoolingSchedule coolingSchedule, int snapshotCollectionInterval)
  {
    this.maxIterations = maxIterations;
    this.initialTemperature = initialTemperature;
    this.initialSolution = initialSolution;
    this.snapshotCollectionInterval = snapshotCollectionInterval;
    this.mutator = new SolutionNeighborhoodExplorer();
    this.coolingSchedule = coolingSchedule;
  }

  public void run()
  {
    result = new ArrayList<>();
    partialProgressList = new ArrayList<>();
    if (initialSolution == null)
    {
      throw new IllegalArgumentException("Expected an initial solution to run Simulated Annealing");
    }

    initialSolution.measureObjectives();

    RefactoringSolution currentSolution = initialSolution;
    RefactoringSolution bestSolution = initialSolution;
    int currentIteration = 1;
    int collectionIntervalCounter = 0;
    while (currentIteration <= maxIterations)
    {
      RefactoringSolution newSolution = mutateSolution(currentSolution);
      newSolution.measureObjectives();

      double currentEnergy = currentSolution.getObjectivesAsSingle();
      double newEnergy = newSolution.getObjectivesAsSingle();

      double temperature = currentTemperture(currentIteration);

      if (acceptanceProbability(currentEnergy, newEnergy, temperature) > Math.random())
      {
        currentSolution = newSolution;
      }

      if (calcEuclideanDistance(currentSolution.getObjectives()) <= calcEuclideanDistance(bestSolution.getObjectives()))
      {
        iterationBestSolution = currentIteration;
        bestSolution = currentSolution;
      }
      currentIteration++;

      if (collectionIntervalCounter == snapshotCollectionInterval) {
        partialProgressList.add(createPartialProgressDTO(bestSolution, currentIteration));
        collectionIntervalCounter = 0;
      } else {
        collectionIntervalCounter++;
      }
    }
    bestSolution.setAttribute("iterationBestSolution", Integer.toString(iterationBestSolution));
    bestSolution.setAttribute("schedule", coolingSchedule.toString());
    bestSolution.setAttribute("initialTemperature", Double.toString(initialTemperature));
    result.add(bestSolution);
  }

  @Override
  public List<PartialProgressSummaryDTO> getPartialProgressList()
  {
    return this.partialProgressList;
  }

  private PartialProgressSummaryDTO createPartialProgressDTO(final RefactoringSolution solution, final int currentIteration)
  {
    PartialProgressSummaryDTO dto = new PartialProgressSummaryDTO();
    dto.currentIteration = currentIteration;
    dto.objectives = solution.getObjectives();
    dto.euclideanDistance =  calcEuclideanDistance(solution.getObjectives());
    return dto;
  }

  private double currentTemperture(int iteration) {
    switch (coolingSchedule) {
      case BOLTZ:
        return initialTemperature / Math.log(iteration);
      case LINEAR:
        return initialTemperature / iteration;
      case EXPONENTIAL: default:
        return initialTemperature * Math.pow(0.98, iteration);
    }
  }

  private double acceptanceProbability(final double currentEnergy, final double newEnergy, final double temperature)
  {
    if (newEnergy <= currentEnergy)
    {
      return 1.0;
    }
    return Math.exp((currentEnergy - newEnergy) / temperature);
  }

  private RefactoringSolution mutateSolution(final RefactoringSolution currentSolution)
  {
    return mutator.getNeighbor(currentSolution);
  }

  @Override
  public List<RefactoringSolution> getResult()
  {
    return this.result;
  }

  @Override
  public String getName()
  {
    return "SA";
  }

  @Override
  public String getDescription()
  {
    return "Simulated Annealing";
  }
}
