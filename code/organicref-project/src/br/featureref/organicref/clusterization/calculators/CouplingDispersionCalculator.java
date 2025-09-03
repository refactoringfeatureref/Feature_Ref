package br.featureref.organicref.clusterization.calculators;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;

public class CouplingDispersionCalculator
{
  public static double getValue(Type type, Project project) {
    return project.getCrossTypesMethodCalls().getDependenciesOfType(type).size();
  }
}
