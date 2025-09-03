package br.featureref.organicref.optimization.solution.generators;

import static br.featureref.organicref.util.ListUtil.getRandomElementFrom;

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
import br.featureref.organicref.refactoring.generators.RefactoringGenerator;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class SingleTypeSolutionGenerator
{
  private Map<SolutionType, RefactoringGenerator> generators = new HashMap<>();
  private Project project;
  private ObjectiveFunction[] objectives;
  private List<Type> typesToRefactor;
  private Map<Type, List<RefactoringSequence>> typeAvailableRefactoringsMap = new HashMap<>();

  public SingleTypeSolutionGenerator(Project project, ObjectiveFunction[] objectives)
  {
    final ConcernBasedTypeElementsClusterDetectorFactory concernClusterizationFactory = new ConcernBasedTypeElementsClusterDetectorFactory();
    this.project = project;
    this.objectives = objectives;
    generators.put(SolutionType.EXTRACT_CLASS, new ExtractClassGenerator(new JaccardClassElementsClustersDetectorFactory()));
    generators.put(SolutionType.MOVE_METHODS, new MoveMethodsGenerator(new ClassElementsClustersDetectorFactory(), project));
    generators.put(SolutionType.CONCERN_EXTRACT_CLASS, new ConcernBasedExtractClassGenerator(concernClusterizationFactory));
    generators.put(SolutionType.CONCERN_MOVE_METHODS, new ConcernBasedMoveMethodsGenerator(concernClusterizationFactory, project));
    //generators.put(SolutionType.EXTRACT_METHODS, new ComplexityBasedExtractMethodGenerator());
    this.typesToRefactor = getTypesToRefactor();
  }

  public RefactoringSolution run(SolutionType solutionType)
  {
    final List<RefactoringSequence<Refactoring>> sequences = typesToRefactor.stream()
        .map(type -> getSequenceOfType(type, solutionType))
        .collect(Collectors.toList());

    RefactoringSolution solution = new RefactoringSolution(sequences.size(), objectives.length);
    solution.setOriginalProject(project);
    solution.setAttribute("baselineType", solutionType.toString());
    for (int i = 0; i < sequences.size(); i++) {
      solution.setVariable(i, sequences.get(i));
    }

    return solution;
  }

  private RefactoringSequence<Refactoring> getSequenceOfType(Type type, SolutionType solutionType)
  {
    final RefactoringGenerator refactoringGenerator = generators.get(solutionType);
    final Optional<RefactoringSequence> generated = refactoringGenerator.generate(type);
    if (generated.isEmpty()) {
      return new MixedOperationsSequence(new ArrayList<>(), type.getFullyQualifiedName());
    } else {
      return generated.get();
    }
  }

  private List<Type> getTypesToRefactor()
  {
    return List.copyOf(project.getContext().getAllTypes());
  }

  public enum SolutionType {
    EXTRACT_CLASS,
    MOVE_METHODS,
    CONCERN_EXTRACT_CLASS,
    CONCERN_MOVE_METHODS,
    EXTRACT_METHODS,
  }
}
