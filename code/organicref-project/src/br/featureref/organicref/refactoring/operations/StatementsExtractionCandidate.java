package br.featureref.organicref.refactoring.operations;

import java.util.List;

public class StatementsExtractionCandidate
{
  private List<String> statementsQualifiedNames;

  public StatementsExtractionCandidate(final List<String> statementsQualifiedNames)
  {
    this.statementsQualifiedNames = statementsQualifiedNames;
  }

  public List<String> getQualifiedNames()
  {
    return statementsQualifiedNames;
  }
}
