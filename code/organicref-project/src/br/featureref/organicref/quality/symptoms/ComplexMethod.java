package br.featureref.organicref.quality.symptoms;

import br.featureref.organicref.model.entities.Method;

public class ComplexMethod extends MethodSymptom
{
  private final double complexity;

  public ComplexMethod(final Method method, double complexity)
  {
    super(method);
    this.complexity = complexity;
  }

  public double getComplexity()
  {
    return complexity;
  }

  @Override
  public String toString()
  {
    return "ComplexMethod{" +
        "complexity=" + getMethod().getFullyQualifiedNameWithParams() +
        "complexity=" + complexity +
        '}';
  }
}
