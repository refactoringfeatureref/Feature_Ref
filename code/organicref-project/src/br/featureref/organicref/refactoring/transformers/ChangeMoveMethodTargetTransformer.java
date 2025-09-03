package br.featureref.organicref.refactoring.transformers;

import static br.featureref.organicref.util.ListUtil.*;

import java.util.List;
import java.util.Optional;

import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.variables.MoveOperationsSequence;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.operations.MoveMethod;
import br.featureref.organicref.refactoring.operations.MoveOperation;

public class ChangeMoveMethodTargetTransformer implements  RefactoringTransformer
{
  public Optional<MoveMethod> execute(final MoveMethod moveMethod)
  {
    List<String> candidates = moveMethod.getAllTargetCandidates();
    if (candidates.size() > 1) {
      candidates.remove(0);
      String selectedTarget = candidates.get(0);
      MoveMethod newMoveMethod = new MoveMethod(moveMethod.getMethodNameWithParams(),
          moveMethod.getOriginalTypeQualifiedName(), selectedTarget, candidates);

      return Optional.of(newMoveMethod);
    }

    return Optional.empty();
  }

  @Override
  public boolean applyTransformation(final RefactoringSolution newSolution, final RefactoringSequence sequence)
  {
    if (sequence instanceof MoveOperationsSequence) {
      MoveOperationsSequence moveSequence = (MoveOperationsSequence) sequence;
      List<MoveOperation> operations = List.copyOf(moveSequence.getRefactorings());
      while (operations.size() > 0)
      {
        MoveOperation operation = getAndRemoveRandomElementFrom(operations);
        if (operation instanceof MoveMethod) {
          final Optional<MoveMethod> optional = execute((MoveMethod) operation);
          if (optional.isPresent()) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
