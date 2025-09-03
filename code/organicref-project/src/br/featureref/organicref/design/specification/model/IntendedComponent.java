package br.featureref.organicref.design.specification.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import br.featureref.organicref.model.entities.Type;

public class IntendedComponent
{
  private String name;
  private List<String> paths;

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  public Collection<String> getPaths()
  {
    return paths;
  }

  public void setPaths(final Collection<String> paths)
  {
    this.paths = List.copyOf(paths);
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    final IntendedComponent component = (IntendedComponent) o;
    return Objects.equals(this.name, component.name);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(name);
  }

  @Override
  public String toString()
  {
    return "IntendedComponent{" +
        "name='" + name + '\'' +
        ", paths=" + paths +
        '}';
  }

  public boolean containsPathOfType(final Type type)
  {
    for (String path : paths) {
      if (type.getFullyQualifiedName().contains(path)) {
        return true;
      }
    }
    return false;
  }
}
