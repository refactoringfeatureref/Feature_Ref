package br.featureref.organicref.refactoring.transformers;

import static br.featureref.organicref.util.ListUtil.getAndRemoveRandomElementFrom;
import static br.featureref.organicref.util.ListUtil.removeRandomElementFrom;

import java.util.ArrayList;
import java.util.List;

import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.variables.ExtractOperationSequence;
import br.featureref.organicref.optimization.solution.variables.MixedOperationsSequence;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.operations.ExtractClass;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class RemoveRefactoringFromExtractTransformer implements RefactoringTransformer
{
  public static final int MIN_REFACTORING_SIZE = 3;

  @Override
  public boolean applyTransformation(final RefactoringSolution newSolution, final RefactoringSequence sequence)
  {
    if (sequence instanceof ExtractOperationSequence || sequence instanceof MixedOperationsSequence) {
      List<Refactoring> refactorings = new ArrayList<>(sequence.getRefactorings());
      while (refactorings.size() > 0) {
        Refactoring element = getAndRemoveRandomElementFrom(refactorings);
        if (element instanceof ExtractClass) {
          ExtractClass extractClass = (ExtractClass) element;
          if (extractClass.getAllExtractedElementsNames().size() >= MIN_REFACTORING_SIZE) {
            List<String> methodsAvailable = new ArrayList<>(extractClass.getExtractedMethodsNames());
            List<String> fieldsAvailable = new ArrayList<>(extractClass.getExtractedFieldsNames());
            if (methodsAvailable.size() > 0 && fieldsAvailable.size() > 0) {
              removeRandomElementFrom(methodsAvailable, fieldsAvailable);
            } else if (methodsAvailable.size() > 0) {
              removeRandomElementFrom(methodsAvailable);
            } else {
              removeRandomElementFrom(fieldsAvailable);
            }

            extractClass.setExtractedMethods(methodsAvailable);
            extractClass.setExtractedFields(fieldsAvailable);
            return true;
          }
        }
      }
    }

    return false;
  }
}
