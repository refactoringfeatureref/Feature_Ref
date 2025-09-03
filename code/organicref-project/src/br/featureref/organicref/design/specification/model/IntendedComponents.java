package br.featureref.organicref.design.specification.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IntendedComponents
{
  private List<IntendedComponent> components = new ArrayList<>();

  public List<IntendedComponent> getComponents()
  {
    return components;
  }

  public void setComponents(final List<IntendedComponent> components)
  {
    this.components = components;
  }

  public Optional<IntendedComponent> getComponentByName(String name)
  {
    return components.stream().filter(c -> c.getName().equals(name)).findFirst();
  }
}
