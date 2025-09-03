package br.featureref.organicref.refactoring.operations;

import br.featureref.organicref.model.entities.Project;

public interface Refactoring
{
  void applyTo(Project project);

  Refactoring copy();

  String getChangeSummary();

  String getName();
}
