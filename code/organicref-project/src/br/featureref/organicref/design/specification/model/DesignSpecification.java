package br.featureref.organicref.design.specification.model;

import java.util.HashMap;

public class DesignSpecification
{
  private final IntendedComponents intendedComponents;
  private final DesignRules designRules;

  public DesignSpecification(final IntendedComponents intendedComponents, final DesignRules designRules)
  {
    this.intendedComponents = intendedComponents;
    this.designRules = designRules;
  }

  public IntendedComponents getIntendedComponents()
  {
    return intendedComponents;
  }

  public DesignRules getDesignRules()
  {
    return designRules;
  }
}
