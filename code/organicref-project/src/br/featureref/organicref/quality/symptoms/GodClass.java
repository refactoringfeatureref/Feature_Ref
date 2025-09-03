package br.featureref.organicref.quality.symptoms;

import br.featureref.organicref.model.entities.Type;

public class GodClass extends TypeSymptom
{
  private double numberOfClientTypes;
  private double numberOfStatements;
  private double cyclomaticComplexity;

  public GodClass(final Type type, final double numberOfClientTypes, final double numberOfStatements, final double cyclomaticComplexity)
  {
    super(type);
    this.numberOfClientTypes = numberOfClientTypes;
    this.numberOfStatements = numberOfStatements;
    this.cyclomaticComplexity = cyclomaticComplexity;
  }

  @Override
  public String toString()
  {
    return "GodClass{" +
        "numberOfClientTypes=" + numberOfClientTypes +
        ", numberOfStatements=" + numberOfStatements +
        ", cyclomaticComplexity=" + cyclomaticComplexity +
        '}';
  }
}
