package br.featureref.organicref.optimization.solution.variables;

import java.util.ArrayList;
import java.util.List;

import br.featureref.organicref.refactoring.operations.Refactoring;

public class MixedOperationsSequence extends RefactoringSequence<Refactoring>
{
  public MixedOperationsSequence(final List<Refactoring> refactorings, final String originalTypeQualifiedName)
  {
    super(refactorings, originalTypeQualifiedName);
  }

  public static MixedOperationsSequence newFrom(final RefactoringSequence sequence1, final RefactoringSequence sequence2)
  {
    if (!sequence1.getOriginalTypeQualifiedName().equals(sequence2.getOriginalTypeQualifiedName())) {
      throw new IllegalArgumentException("It is not possible to merge sequences related to different types");
    }
    List<Refactoring> newSequence = new ArrayList<>(sequence1.getRefactorings());
    newSequence.addAll(sequence2.getRefactorings());

    return new MixedOperationsSequence(newSequence, sequence1.getOriginalTypeQualifiedName());
  }
}
