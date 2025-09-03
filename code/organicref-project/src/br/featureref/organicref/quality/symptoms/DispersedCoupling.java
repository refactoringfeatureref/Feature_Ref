package br.featureref.organicref.quality.symptoms;

import br.featureref.organicref.model.entities.Type;

public class DispersedCoupling extends TypeSymptom
{
  private double couplingIntensity;
  private double couplingDispersion;
  private double cyclomaticComplexity;

  public DispersedCoupling(final Type type, final double couplingIntensity, final double couplingDispersion, final double cyclomaticComplexity)
  {
    super(type);
    this.couplingIntensity = couplingIntensity;
    this.couplingDispersion = couplingDispersion;
    this.cyclomaticComplexity = cyclomaticComplexity;
  }

  @Override
  public String toString()
  {
    return "DispersedCoupling{" +
        "couplingIntensity=" + couplingIntensity +
        ", couplingDispersion=" + couplingDispersion +
        ", cyclomaticComplexity=" + cyclomaticComplexity +
        '}';
  }
}
