package br.featureref.organicref.optimization.solution.variables;

import java.util.List;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class RefactoringSequence<T extends Refactoring>
{
  private List<T> refactorings;
  private String originalTypeQualifiedName;

  public RefactoringSequence(final List<T> refactorings, final String originalTypeQualifiedName)
  {
    this.refactorings = refactorings;
    this.originalTypeQualifiedName = originalTypeQualifiedName;
  }

  public void applyTo(Project project) {
    refactorings.stream().forEach(r -> r.applyTo(project));
  }

  public String getOriginalTypeQualifiedName() {
    return this.originalTypeQualifiedName;
  }

  public List<T> getRefactorings() { return this.refactorings; }

  public void removeRefactoring(T refactoring) {
    this.refactorings.remove(refactoring);
  }

  public void addRefactoring(T refactoring) {
    if (refactoring != null) {
      this.refactorings.add(refactoring);
    }
  }

  public RefactoringSequence copy()
  {
    List<Refactoring> copyOfRefactorings = refactorings.stream().map(r -> r.copy()).collect(Collectors.toList());
    return new RefactoringSequence(copyOfRefactorings, originalTypeQualifiedName);
  }

  @Override
  public String toString()
  {
    return "RefactoringSequence{" +
        "refactorings=" + refactorings +
        ", originalTypeQualifiedName='" + originalTypeQualifiedName + '\'' +
        '}';
  }

  public String getDescription()
  {
    return originalTypeQualifiedName + ":" + refactorings.stream().map(r -> r.getClass().toString()).collect(Collectors.joining(","));
  }
}
