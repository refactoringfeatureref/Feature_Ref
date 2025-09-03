package br.featureref.organicref.optimization.solution.generators;

import static br.featureref.organicref.util.ListUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import br.featureref.organicref.clusterization.basic.ClassElementsClustersDetectorFactory;
import br.featureref.organicref.clusterization.concernbased.ConcernBasedTypeElementsClusterDetectorFactory;
import br.featureref.organicref.clusterization.jaccard.JaccardClassElementsClustersDetectorFactory;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.optimization.fitness.ObjectiveFunction;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.variables.MixedOperationsSequence;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.generators.ComplexityBasedExtractMethodGenerator;
import br.featureref.organicref.refactoring.generators.ConcernBasedExtractClassGenerator;
import br.featureref.organicref.refactoring.generators.ConcernBasedMoveMethodsGenerator;
import br.featureref.organicref.refactoring.generators.ExtractClassGenerator;
import br.featureref.organicref.refactoring.generators.MoveMethodsGenerator;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class RandomSolutionGenerator
{
  private ExtractClassGenerator extractClassGenerator;
  private MoveMethodsGenerator moveMethodsGenerator;
  private ConcernBasedExtractClassGenerator concernBasedECGenerator;
  private ConcernBasedMoveMethodsGenerator concernBasedMovesGenerator;
  private ComplexityBasedExtractMethodGenerator extractMethodGenerator;
  private Project project;
  private ObjectiveFunction[] objectives;
  private List<Type> typesToRefactor;
  private Map<Type, List<RefactoringSequence>> typeAvailableRefactoringsMap = new HashMap<>();
  private boolean useConcernBasedHeuristics;

  public RandomSolutionGenerator(Project project, ObjectiveFunction[] objectives)
  {
    this(project, objectives, true);
  }

  public RandomSolutionGenerator(final Project project, final ObjectiveFunction[] objectives, final boolean useConcernBasedHeuristics)
  {
    this.useConcernBasedHeuristics = useConcernBasedHeuristics;
    final ConcernBasedTypeElementsClusterDetectorFactory concernClusterizationFactory = new ConcernBasedTypeElementsClusterDetectorFactory();
    this.project = project;
    this.objectives = objectives;
    this.extractClassGenerator = new ExtractClassGenerator(new JaccardClassElementsClustersDetectorFactory());
    this.moveMethodsGenerator = new MoveMethodsGenerator(new ClassElementsClustersDetectorFactory(), project);
    this.concernBasedECGenerator = new ConcernBasedExtractClassGenerator(concernClusterizationFactory);
    this.concernBasedMovesGenerator = new ConcernBasedMoveMethodsGenerator(concernClusterizationFactory, project);
    this.extractMethodGenerator = new ComplexityBasedExtractMethodGenerator();
    this.typesToRefactor = getTypesToRefactor();
  }

  public RefactoringSolution run()
  {
    final List<RefactoringSequence<Refactoring>> sequences = typesToRefactor.stream()
        .map(this::getRandomSequence)
        .filter(e -> !e.isEmpty())
        .map(e -> e.get())
        .collect(Collectors.toList());

    RefactoringSolution solution = new RefactoringSolution(sequences.size(), objectives.length);
    solution.setOriginalProject(project);
    for (int i = 0; i < sequences.size(); i++) {
      solution.setVariable(i, sequences.get(i));
    }

    return solution;
  }

  private Optional<RefactoringSequence<Refactoring>> getRandomSequence(Type type)
  {
    fillAvailableRefactoringsMap(type);
    final List<RefactoringSequence> availableSequences = typeAvailableRefactoringsMap.get(type);
    if (availableSequences != null && availableSequences.size() > 0) {
      return Optional.of(getRandomElementFrom(availableSequences));
    }
    return Optional.of(new MixedOperationsSequence(new ArrayList<>(), type.getFullyQualifiedName()));
  }

  private void fillAvailableRefactoringsMap(final Type type)
  {
    if (!typeAvailableRefactoringsMap.containsKey(type))
    {
      List<RefactoringSequence> availableSequences = new ArrayList<>();
      extractClassGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
      moveMethodsGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
      if (useConcernBasedHeuristics)
      {
        concernBasedECGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
        concernBasedMovesGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
      }
      typeAvailableRefactoringsMap.put(type, availableSequences);
    }
  }

  private List<Type> getTypesToRefactor()
  {
    return List.copyOf(project.getContext().getAllTypes());
  }
}
