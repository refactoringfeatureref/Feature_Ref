package br.featureref.organicref.design.specification.model;

import java.util.Objects;

import br.featureref.organicref.model.entities.Type;

public class DesignViolation
{
  private Type typeFrom;
  private Type typeTo;
  private DesignRule violatedRule;

  @Override
  public String toString()
  {
    return "DesignViolation{" +
        "typeFrom=" + typeFrom +
        ", typeTo=" + typeTo +
        ", violatedRule=" + violatedRule +
        '}';
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
    final DesignViolation that = (DesignViolation) o;
    return typeFrom.equals(that.typeFrom) && typeTo.equals(that.typeTo) && violatedRule.equals(that.violatedRule);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(typeFrom, typeTo, violatedRule);
  }

  public DesignViolation(final Type typeFrom, final Type typeTo, final DesignRule violatedRule)
  {
    this.typeFrom = typeFrom;
    this.typeTo = typeTo;
    this.violatedRule = violatedRule;
  }
}
