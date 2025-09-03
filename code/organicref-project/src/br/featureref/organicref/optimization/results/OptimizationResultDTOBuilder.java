package br.featureref.organicref.optimization.results;

import static br.featureref.organicref.util.StatisticsUtil.calcEuclideanDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.optimization.fitness.LackOfCohesionObjective;
import br.featureref.organicref.optimization.fitness.MetricsIntensityObjective;
import br.featureref.organicref.optimization.fitness.NumberOfConcernsObjective;
import br.featureref.organicref.optimization.fitness.DensityObjective;
import br.featureref.organicref.optimization.results.dtos.OptimizationResultDTO;
import br.featureref.organicref.optimization.results.dtos.PartialProgressSummaryDTO;
import br.featureref.organicref.optimization.results.dtos.ProjectSummaryDTO;
import br.featureref.organicref.optimization.results.dtos.RefactoringDTO;
import br.featureref.organicref.optimization.results.dtos.RefactoringSequenceDTO;
import br.featureref.organicref.optimization.results.dtos.SimplifiedResultDTO;
import br.featureref.organicref.optimization.results.dtos.SimplifiedSolutionDTO;
import br.featureref.organicref.optimization.results.dtos.SolutionDTO;
import br.featureref.organicref.optimization.results.dtos.context.ConcernDTO;
import br.featureref.organicref.optimization.results.dtos.context.ContextDTO;
import br.featureref.organicref.optimization.results.dtos.context.ContextImpactSummaryDTO;
import br.featureref.organicref.optimization.results.dtos.context.TypeInContextDTO;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.quality.metrics.TypeMetrics;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class OptimizationResultDTOBuilder
{
  private Project projectBeforeRefactoring;
  private List<RefactoringSolution> population;
  private Long computingTime = null;
  private String algorithmName;
  private int maxEvaluations;
  private int initialPopulationSize;
  private OptimizationResultDTO resultDTO;
  private SimplifiedResultDTO simplifiedResultDTO;

  public ContextImpactSummaryDTO getContextImpactSummaryDTO()
  {
    return contextImpactSummaryDTO;
  }

  private ContextImpactSummaryDTO contextImpactSummaryDTO;

  public OptimizationResultDTOBuilder(Project projectBeforeRefactoring, List<RefactoringSolution> population) {
    this.projectBeforeRefactoring = projectBeforeRefactoring;
    this.population = population;
  }
  
  public OptimizationResultDTOBuilder computingTime(long computingTime) {
    this.computingTime = computingTime;
    return this;
  }
  
  public OptimizationResultDTOBuilder algorithmName(String name) {
    this.algorithmName = name;
    return this;
  }

  public OptimizationResultDTOBuilder maxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;
    return this;
  }

  public OptimizationResultDTOBuilder initialPopulationSize(final int size)
  {
    this.initialPopulationSize = size;
    return this;
  }

  public OptimizationResultDTOBuilder build() {
    resultDTO = new OptimizationResultDTO();
    simplifiedResultDTO = new SimplifiedResultDTO();
    contextImpactSummaryDTO = new ContextImpactSummaryDTO();

    resultDTO.executionId = UUID.randomUUID();
    simplifiedResultDTO.executionId = resultDTO.executionId;
    contextImpactSummaryDTO.executionId = resultDTO.executionId;

    resultDTO.computingTime = computingTime;
    resultDTO.algorithm = algorithmName;
    resultDTO.maxEvaluations = maxEvaluations;
    resultDTO.initialPopulationSize = initialPopulationSize;
    resultDTO.finalPopulationSize = population.size();

    resultDTO.projectBeforeRefactoring = mapProjectToSummary(projectBeforeRefactoring);
    contextImpactSummaryDTO.beforeRefactoring = mapProjectToContextSummary(projectBeforeRefactoring);

    mapSolutionsToDTOs(population);

    return this;
  }

  private ContextDTO mapProjectToContextSummary(final Project project)
  {
    ContextDTO dto = new ContextDTO();
    dto.typesInContext = new ArrayList<>();

    for (Type type : project.getContext().getTypes()) {
      TypeInContextDTO typeDTO = new TypeInContextDTO();
      typeDTO.name = type.getFullyQualifiedName();
      typeDTO.symptoms = project.getProjectSymptoms().getSymptomsOfType(type).stream().map(s -> s.toString()).collect(Collectors.toList());
      typeDTO.concerns = type.getElementConcerns().stream().map(ec -> new ConcernDTO(ec)).collect(Collectors.toList());
      typeDTO.metrics = new HashMap<TypeMetrics, Double>();
      Arrays.stream(TypeMetrics.values())
          .forEach(tm -> typeDTO.metrics.put(tm, type.getMetric(tm)));
      dto.typesInContext.add(typeDTO);
    }

    return dto;
  }

  public OptimizationResultDTO getResult() {
    return resultDTO;
  }

  public SimplifiedResultDTO getSimplifiedResult() {
    return simplifiedResultDTO;
  }

  private void mapSolutionsToDTOs(final List<RefactoringSolution> population)
  {
    List<SolutionDTO> dtos = new ArrayList<>();
    List<SimplifiedSolutionDTO> simplifiedDtos = new ArrayList<>();
    List<ContextDTO> contextDTOS = new ArrayList<>();

    for (RefactoringSolution solution : population)
    {
      SolutionDTO solutionDTO = new SolutionDTO();
      SimplifiedSolutionDTO simplifiedDTO = new SimplifiedSolutionDTO();

      solutionDTO.solutionId = UUID.randomUUID();
      simplifiedDTO.solutionId = solutionDTO.solutionId;

      solutionDTO.generation = solution.getGeneration();
      solutionDTO.numberOfRefactorings = solution.getVariables()
          .stream()
          .map(s -> s.getRefactorings().size())
          .reduce(0, Integer::sum);
      for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
        solutionDTO.objectives.put(solution.objectiveFunctions[i].getObjectiveName(), solution.getObjective(i));
        simplifiedDTO.objectives.put(solution.objectiveFunctions[i].getObjectiveName(), solution.getObjective(i));
      }
      solutionDTO.euclideanDistance = calcEuclideanDistance(solution.getObjectives());
      simplifiedDTO.euclideanDistance = solutionDTO.euclideanDistance;
      solutionDTO.sequences = mapSequencesToDTOs(solution.getVariables());
      solutionDTO.additionalInformation = mapAttributesToAdditionalInformation(solution.getAttributes());
      dtos.add(solutionDTO);
      simplifiedDtos.add(simplifiedDTO);

      solution.createRefactoredProject();
      final ContextDTO contextDTO = mapProjectToContextSummary(solution.getRefactoredProject());
      contextDTO.solutionId = solutionDTO.solutionId;
      contextDTOS.add(contextDTO);
    }

    Collections.sort(dtos, (s1, s2) -> Double.compare(s1.euclideanDistance, s2.euclideanDistance));
    Collections.sort(simplifiedDtos, (s1, s2) -> Double.compare(s1.euclideanDistance, s2.euclideanDistance));

    resultDTO.population = dtos;
    simplifiedResultDTO.solutions = simplifiedDtos;
    contextImpactSummaryDTO.solutionsSummaries = contextDTOS;
  }

  private Map<String, Object> mapAttributesToAdditionalInformation(final Map<Object, Object> attributes)
  {
    Map<String, Object> information = new HashMap<>();
    for (var entry : attributes.entrySet()) {
      if (entry.getValue() instanceof String) {
        information.put(entry.getKey().toString(), entry.getValue().toString());
      } else if (entry.getValue() instanceof DescriptiveStatistics) {
        DescriptiveStatistics stats = (DescriptiveStatistics) entry.getValue();
        information.put(entry.getKey().toString(), stats.getMean());
      }
    }
    return information;
  }

  private List<RefactoringSequenceDTO> mapSequencesToDTOs(final List<RefactoringSequence<Refactoring>> variables)
  {
    List<RefactoringSequenceDTO> dtos = new ArrayList<>();
    for (RefactoringSequence<Refactoring> sequence : variables) {
      if (sequence.getRefactorings().size() > 0)
      {
        RefactoringSequenceDTO sequenceDTO = new RefactoringSequenceDTO();
        sequenceDTO.refactoredTypeName = sequence.getOriginalTypeQualifiedName();
        sequenceDTO.refactorings = mapRefactoringsToDTOs(sequence.getRefactorings());
        dtos.add(sequenceDTO);
      }
    }
    return dtos;
  }

  private List<RefactoringDTO> mapRefactoringsToDTOs(final List<Refactoring> refactorings)
  {
    List<RefactoringDTO> dtos = new ArrayList<>();
     for (Refactoring refactoring : refactorings) {
       RefactoringDTO refactoringDTO = new RefactoringDTO();
       refactoringDTO.refactoringKind = refactoring.getName();
       refactoringDTO.change = refactoring.getChangeSummary();
       dtos.add(refactoringDTO);
     }
     return dtos;
  }

  private ProjectSummaryDTO mapProjectToSummary(final Project project)
  {
    ProjectSummaryDTO dto = new ProjectSummaryDTO();

    dto.meanDensity = new DensityObjective().evaluate(project).getMean();
    dto.meanNumConcerns = new NumberOfConcernsObjective().evaluate(project).getMean();
    dto.meanMetricsIntensity = new MetricsIntensityObjective().evaluate(project).getMean();
    dto.meanLackOfCohesion = new LackOfCohesionObjective().evaluate(project).getMean();
    return dto;
  }
}
