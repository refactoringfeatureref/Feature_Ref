package br.featureref.organicref.quality.symptoms;

import java.util.List;

import br.featureref.organicref.model.entities.Type;

public class ComplexClass extends TypeSymptom
{
  private final double complexity;
  private List<ComplexMethod> methodSmells;

  public ComplexClass(final Type type, double complexity, final List<ComplexMethod> methodSmells)
  {
    super(type);
    this.complexity = complexity;
    this.methodSmells = methodSmells;
  }

  public double getComplexity()
  {
    return complexity;
  }

  @Override
  public String toString()
  {
    return "ComplexClass{" +
        "complexity=" + complexity +
        ", methodSmells=" + methodSmells +
        '}';
  }
}
