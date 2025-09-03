package br.featureref.organicref.refactoring.generators;

import java.util.Optional;

import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;

public interface RefactoringGenerator<T extends RefactoringSequence>
{
  Optional<T> generate(Type type);
}
