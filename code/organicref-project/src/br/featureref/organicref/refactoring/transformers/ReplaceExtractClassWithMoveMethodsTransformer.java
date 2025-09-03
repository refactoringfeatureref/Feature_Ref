package br.featureref.organicref.refactoring.transformers;

import static br.featureref.organicref.util.ListUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.variables.ExtractOperationSequence;
import br.featureref.organicref.optimization.solution.variables.MixedOperationsSequence;
import br.featureref.organicref.optimization.solution.variables.MoveOperationsSequence;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.operations.ExtractClass;
import br.featureref.organicref.refactoring.generators.MoveMethodsGenerator;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class ReplaceExtractClassWithMoveMethodsTransformer implements RefactoringTransformer
{
  public Optional<MoveOperationsSequence> execute(ExtractClass extractClass, Project project)
  {
    final String typeQualifiedName = extractClass.getOriginalTypeQualifiedName();
    final Optional<Type> optionalType = project.getTypeByQualifiedName(typeQualifiedName);
    if (!optionalType.isEmpty()) {
      final Type type = optionalType.get();
      MoveMethodsGenerator generator = new MoveMethodsGenerator(project);
      return generator.generate(type);
    }
    return Optional.empty();
  }

  @Override
  public boolean applyTransformation(final RefactoringSolution newSolution, final RefactoringSequence sequence)
  {
    if (sequence instanceof ExtractOperationSequence || sequence instanceof MixedOperationsSequence) {
      List<Refactoring> refactorings = new ArrayList<>(sequence.getRefactorings());
      while (refactorings.size() > 0) {
        Refactoring element = getAndRemoveRandomElementFrom(refactorings);
        if (element instanceof ExtractClass) {
          Optional<MoveOperationsSequence> moveSequence = execute((ExtractClass) element, newSolution.getOriginalProject());
          if (moveSequence.isPresent()) {
            sequence.removeRefactoring(element);
            MixedOperationsSequence newSequence = MixedOperationsSequence.newFrom(sequence, moveSequence.get());
            newSolution.replaceVariable(sequence, newSequence);
            return true;
          }
        }
      }
    }
    return false;
  }
}
