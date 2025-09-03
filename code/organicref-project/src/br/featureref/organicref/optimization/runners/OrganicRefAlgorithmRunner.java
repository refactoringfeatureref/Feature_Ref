package br.featureref.organicref.optimization.runners;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.AbstractSimulatedAnnealing;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.mosa.MOSABuilder;
import org.uma.jmetal.mosa.MOSAHybridNSGAII;
import org.uma.jmetal.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.optimization.algorithms.AuditedAlgorithm;
import br.featureref.organicref.optimization.algorithms.BaselineAlgorithm;
import br.featureref.organicref.optimization.algorithms.localsearch.CoolingSchedule;
import br.featureref.organicref.optimization.algorithms.localsearch.SimulatedAnnealingAlgorithm;
import br.featureref.organicref.optimization.algorithms.globalsearch.AuditedNSGAIIIBuilder;
import br.featureref.organicref.optimization.results.dtos.OptimizationResultDTO;
import br.featureref.organicref.optimization.results.OptimizationResultDTOBuilder;
import br.featureref.organicref.optimization.results.dtos.PartialProgressSummaryDTO;
import br.featureref.organicref.optimization.results.dtos.SimplifiedResultDTO;
import br.featureref.organicref.optimization.fitness.ObjectiveFunction;
import br.featureref.organicref.optimization.operators.crossover.RefactoringSequenceCrossoverOperator;
import br.featureref.organicref.optimization.operators.mutation.RefactoringSequenceMutationOperator;
import br.featureref.organicref.optimization.problem.RefactoringProblem;
import br.featureref.organicref.optimization.results.dtos.context.ContextImpactSummaryDTO;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.generators.RandomSolutionGenerator;
import br.featureref.organicref.util.OrganicRefOptions;
import br.featureref.organicref.util.InOutUtil;

/**
 * Abstract class for Runner classes
 * Based on the AbstractALgorithmRunner class from JMetal
 */
public class OrganicRefAlgorithmRunner
{
	private Project project;
	private int maxEvaluations;
	private int populationSize;
	private AlgorithmTypeEnum algorithmTypeEnum;
	private CoolingSchedule coolingSchedule;
	private int snapshotCollectionInterval;

	public OrganicRefAlgorithmRunner(final Project project, AlgorithmTypeEnum algorithmTypeEnum) {
		this.project = project;
		this.maxEvaluations = OrganicRefOptions.getInstance().getMaxEvaluations(1000);
		this.populationSize = OrganicRefOptions.getInstance().getPopulationSize(50);
		this.coolingSchedule = OrganicRefOptions.getInstance().getCoolingSchedule(CoolingSchedule.EXPONENTIAL);
		this.algorithmTypeEnum = algorithmTypeEnum;
		//We are aiming at 10 collections. May change in the future if necessary
		this.snapshotCollectionInterval = 100;
	}

	public void run()
	{
		Algorithm<List<RefactoringSolution>> selectedAlgo = null;
		if (algorithmTypeEnum.equals(AlgorithmTypeEnum.BASELINE_NF)) {
			selectedAlgo = runNoFeatureBaseline();
		} else if (algorithmTypeEnum.equals(AlgorithmTypeEnum.BASELINE)) {
			selectedAlgo = runBaseline();
		} else if (algorithmTypeEnum.equals(AlgorithmTypeEnum.SA)) {
			selectedAlgo = runSimulatedAnnealing();
		} else {
			selectedAlgo = runJMetalAlgorithm();
		}
		runAndSaveResult(selectedAlgo);
	}

	private Algorithm<List<RefactoringSolution>> runNoFeatureBaseline()
	{
		return new BaselineAlgorithm(project, false);
	}

	private Algorithm<List<RefactoringSolution>> runBaseline()
	{
		return new BaselineAlgorithm(project, true);
	}

	private Algorithm<List<RefactoringSolution>> runSimulatedAnnealing()
	{
		RandomSolutionGenerator generator = new RandomSolutionGenerator(project, ObjectiveFunction.getAllObjectiveFunctions());
		RefactoringSolution initialSolution = generator.run();
		this.populationSize = 1;
		return new SimulatedAnnealingAlgorithm(maxEvaluations, 100, initialSolution, coolingSchedule, snapshotCollectionInterval);
	}

	public Algorithm<List<RefactoringSolution>> runJMetalAlgorithm() {
		Problem<RefactoringSolution> problem;
		CrossoverOperator<RefactoringSolution> crossover;
		MutationOperator<RefactoringSolution> mutation;
		SelectionOperator<List<RefactoringSolution>, RefactoringSolution> selection;
		Algorithm<List<RefactoringSolution>> algorithm;

		ObjectiveFunction[] objectiveFunctions = ObjectiveFunction.getAllObjectiveFunctions();

		problem = new RefactoringProblem(project, objectiveFunctions);
		crossover = new RefactoringSequenceCrossoverOperator();
		mutation = new RefactoringSequenceMutationOperator();
		selection = new BinaryTournamentSelection<RefactoringSolution>(
				new RankingAndCrowdingDistanceComparator<RefactoringSolution>());

		return getAlgorithm(problem, crossover, mutation, selection);
	}

	private void runAndSaveResult(final Algorithm<List<RefactoringSolution>> algorithm)
	{
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

		List<RefactoringSolution> population = algorithm.getResult();
		long computingTime = algorithmRunner.getComputingTime();
		final OptimizationResultDTOBuilder builder = new OptimizationResultDTOBuilder(project, population)
				.computingTime(computingTime)
				.algorithmName(algorithm.getName())
				.maxEvaluations(maxEvaluations)
				.initialPopulationSize(populationSize)
				.build();

		OptimizationResultDTO resultDTO = builder.getResult();
		SimplifiedResultDTO simplifiedResultDTO = builder.getSimplifiedResult();
		ContextImpactSummaryDTO contextImpactSummaryDTO = builder.getContextImpactSummaryDTO();
		List<PartialProgressSummaryDTO> partialProgress = getPartialProgressSummaries(algorithm);

		if (partialProgress.size() > 0)
		{
			InOutUtil.saveAsJsonFileWithCurrentDate(partialProgress, "fitness_snapshots.json");
		}
		InOutUtil.saveAsJsonFileWithCurrentDate(contextImpactSummaryDTO, ContextImpactSummaryDTO.CONTEXT_FILE_SUFFIX);
		InOutUtil.saveAsJsonFileWithCurrentDate(resultDTO, OptimizationResultDTO.RESULTS_FILE_SUFFIX);
		InOutUtil.saveAsJsonFileWithCurrentDate(simplifiedResultDTO, SimplifiedResultDTO.SIMPLIFIED_RESULTS_FILE_SUFFIX);
	}

	private List<PartialProgressSummaryDTO> getPartialProgressSummaries(final Algorithm<List<RefactoringSolution>> algorithm)
	{
		if (algorithm instanceof AuditedAlgorithm) {
			return ((AuditedAlgorithm) algorithm).getPartialProgressList();
		}
		return new ArrayList<>();
	}

	private Algorithm<List<RefactoringSolution>> getAlgorithm(final Problem<RefactoringSolution> problem, final CrossoverOperator<RefactoringSolution> crossover,
			final MutationOperator<RefactoringSolution> mutation, final SelectionOperator<List<RefactoringSolution>, RefactoringSolution> selection)
	{
		switch (algorithmTypeEnum) {
			case NSGAIII:
				return new NSGAIIIBuilder<RefactoringSolution>(problem)
						.setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(2))
						.setCrossoverOperator(crossover)
						.setMutationOperator(mutation)
						.setSelectionOperator(selection)
						.setPopulationSize(populationSize)
						.setMaxIterations(maxEvaluations)
						.setNumberOfDivisions(problem.getNumberOfObjectives())
						.build();
			case RANDOM:
				return new RandomSearchBuilder<RefactoringSolution>(problem)
						.setMaxEvaluations(maxEvaluations)
						.build();
			case MOSA:
				return new MOSABuilder<RefactoringSolution>(problem, mutation)
						.setPopulationSize(populationSize)
						.setInitialTemperature(maxEvaluations)
						.setFinalTemperature(0.01)
						.setMetropolisLength(50)
						.setAlpha(0.98)
						.setHardLimit(100)
						.setSoftLimit(150)
						.setVariant(MOSABuilder.MOSAVariant.MOSA).build();
			case AMOSA:
				return new MOSABuilder<RefactoringSolution>(problem, mutation)
						.setPopulationSize(populationSize)
						.setInitialTemperature(maxEvaluations)
						.setFinalTemperature(0.01)
						.setMetropolisLength(50)
						.setAlpha(0.98)
						.setHardLimit(100)
						.setSoftLimit(150)
						.setVariant(MOSABuilder.MOSAVariant.AMOSA).build();
			case MOSAHybridNSGAII:
				return new MOSAHybridNSGAII<RefactoringSolution>(problem, populationSize, maxEvaluations, 0.01,
			50, 0.98, mutation, new AcceptanceCriteria<RefactoringSolution>(AbstractSimulatedAnnealing.TransitionRule.RandomCost));
			default:
			case NSGAII:
				return new NSGAIIBuilder<RefactoringSolution>(problem, crossover, mutation, populationSize)
						.setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(2))
						.setSelectionOperator(selection)
						.setMaxEvaluations(maxEvaluations).build();

		}
	}
}
