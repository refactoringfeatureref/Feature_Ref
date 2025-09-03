package br.featureref.organicref.optimization.solution.variables;

import java.util.List;

import br.featureref.organicref.refactoring.operations.MoveOperation;

public class MoveOperationsSequence extends RefactoringSequence<MoveOperation>
{
  public MoveOperationsSequence(final List<MoveOperation> refactorings,
      final String originalTypeQualifiedName)
  {
    super(refactorings, originalTypeQualifiedName);
  }

  public MoveOperationsSequence(final MoveOperation refactoring,
      final String originalTypeQualifiedName)
  {
    super(List.of(refactoring), originalTypeQualifiedName);
  }
}
