package br.featureref.organicref.optimization.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.optimization.fitness.ObjectiveFunction;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.generators.RandomSolutionGenerator;
import br.featureref.organicref.optimization.solution.generators.SingleTypeSolutionGenerator;

public class BaselineAlgorithm implements Algorithm<List<RefactoringSolution>>
{
  private RandomSolutionGenerator randomSolutionGenerator;
  private SingleTypeSolutionGenerator singleTypeSolutionGenerator;
  private boolean useConcernBasedHeuristics;
  private Project project;
  private List<RefactoringSolution> solutions = null;

  public BaselineAlgorithm(final Project project, boolean useConcernBasedHeuristics)
  {
    this.project = project;
    this.randomSolutionGenerator = new RandomSolutionGenerator(project, ObjectiveFunction.getAllObjectiveFunctions(), useConcernBasedHeuristics);
    this.singleTypeSolutionGenerator = new SingleTypeSolutionGenerator(project, ObjectiveFunction.getAllObjectiveFunctions());
    this.useConcernBasedHeuristics = useConcernBasedHeuristics;
  }

  @Override
  public void run()
  {
    solutions = new ArrayList<>();

    int n = 1;
    while (n <= 30)
    {
      solutions.add(getRandomSolution());
      n++;
    }
    solutions.addAll(getIndividualSolutions());
  }

  private Collection<? extends RefactoringSolution> getIndividualSolutions()
  {
    Collection<RefactoringSolution> solutions = new ArrayList<>();
    solutions.add(singleTypeSolutionGenerator.run(SingleTypeSolutionGenerator.SolutionType.EXTRACT_CLASS));
    solutions.add(singleTypeSolutionGenerator.run(SingleTypeSolutionGenerator.SolutionType.MOVE_METHODS));
    if (useConcernBasedHeuristics)
    {
      solutions.add(singleTypeSolutionGenerator.run(SingleTypeSolutionGenerator.SolutionType.CONCERN_EXTRACT_CLASS));
      solutions.add(singleTypeSolutionGenerator.run(SingleTypeSolutionGenerator.SolutionType.CONCERN_MOVE_METHODS));
    }
    for (final RefactoringSolution solution : solutions)
    {
      solution.measureObjectives();
    }

    return solutions;
  }

  private RefactoringSolution getRandomSolution()
  {
    final RefactoringSolution randomSolution = randomSolutionGenerator.run();
    randomSolution.setAttribute("baselineType", "RANDOM_SELECTION");
    randomSolution.measureObjectives();
    return randomSolution;
  }

  @Override
  public List<RefactoringSolution> getResult()
  {
    return solutions;
  }

  @Override
  public String getName()
  {
    return "Baseline" + (useConcernBasedHeuristics ? "" : "_NF");
  }

  @Override
  public String getDescription()
  {
    return "Baseline solutions generated without optimization";
  }
}
