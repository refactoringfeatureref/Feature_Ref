package br.featureref.organicref.optimization.solution.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.featureref.organicref.clusterization.basic.ClassElementsClustersDetectorFactory;
import br.featureref.organicref.clusterization.concernbased.ConcernBasedTypeElementsClusterDetectorFactory;
import br.featureref.organicref.clusterization.jaccard.JaccardClassElementsClustersDetectorFactory;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.optimization.solution.variables.MixedOperationsSequence;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.generators.ComplexityBasedExtractMethodGenerator;
import br.featureref.organicref.refactoring.generators.ConcernBasedExtractClassGenerator;
import br.featureref.organicref.refactoring.generators.ConcernBasedMoveMethodsGenerator;
import br.featureref.organicref.refactoring.generators.ExtractClassGenerator;
import br.featureref.organicref.refactoring.generators.MoveMethodsGenerator;

public class SequenceGenerator
{
  private ExtractClassGenerator extractClassGenerator;
  private MoveMethodsGenerator moveMethodsGenerator;
  private ConcernBasedExtractClassGenerator concernBasedECGenerator;
  private ConcernBasedMoveMethodsGenerator concernBasedMovesGenerator;
  private ComplexityBasedExtractMethodGenerator extractMethodGenerator;

  public SequenceGenerator(Project project) {
    final ConcernBasedTypeElementsClusterDetectorFactory concernClusterizationFactory = new ConcernBasedTypeElementsClusterDetectorFactory();
    this.extractClassGenerator = new ExtractClassGenerator(new JaccardClassElementsClustersDetectorFactory());
    this.moveMethodsGenerator = new MoveMethodsGenerator(new ClassElementsClustersDetectorFactory(), project);
    this.concernBasedECGenerator = new ConcernBasedExtractClassGenerator(concernClusterizationFactory);
    this.concernBasedMovesGenerator = new ConcernBasedMoveMethodsGenerator(concernClusterizationFactory, project);
    this.extractMethodGenerator = new ComplexityBasedExtractMethodGenerator();
  }

  public List<RefactoringSequence> getAvailableSequences(Type type)
  {
    List<RefactoringSequence> availableSequences = new ArrayList<>();
    extractClassGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
    moveMethodsGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
    concernBasedECGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
    concernBasedMovesGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
    //extractMethodGenerator.generate(project, type).ifPresent(r -> availableSequences.add(r));

    if (availableSequences.size() > 0) {
      return availableSequences;
    }
    return Arrays.asList(new MixedOperationsSequence(new ArrayList<>(), type.getFullyQualifiedName()));
  }
}
