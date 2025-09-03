package br.featureref.organicref.clusterization.calculators;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;

public class LCOM3Calculator
{
  public static double getValue(Type type) {

    final double numMethods = type.getMethods().size();
    final double numFields = type.getFields().size();
    final double sumInternalUses = type.getFields()
        .stream()
        .map(f -> type.getLocalMethodsFieldsRelationships().getMethodsThatUse(f).size())
        .reduce(0, Integer::sum);

    if (numFields > 0 && numMethods > 1)
    {
      return (numMethods - sumInternalUses / numFields) / (numMethods - 1);
    } else {
      return 0.0;
    }
  }
}
