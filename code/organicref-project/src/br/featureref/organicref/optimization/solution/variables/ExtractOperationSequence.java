package br.featureref.organicref.optimization.solution.variables;

import java.util.List;

import br.featureref.organicref.refactoring.operations.ExtractTypeOperation;

public class ExtractOperationSequence extends RefactoringSequence<ExtractTypeOperation>
{
  public ExtractOperationSequence(final List<ExtractTypeOperation> refactorings, final String originalTypeQualifiedName)
  {
    super(refactorings, originalTypeQualifiedName);
  }

  public ExtractOperationSequence(final ExtractTypeOperation refactoring, final String originalTypeQualifiedName)
  {
    super(List.of(refactoring), originalTypeQualifiedName);
  }
}
