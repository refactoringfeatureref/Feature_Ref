package br.featureref.organicref.refactoring.transformers;

import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;

public interface RefactoringTransformer
{
  boolean applyTransformation(RefactoringSolution newSolution, RefactoringSequence sequence);
}
