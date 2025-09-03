package br.featureref.organicref.design.specification.parsers;

import br.featureref.organicref.design.specification.model.IntendedComponents;

public class IntendedComponentsParser extends JsonSpecificationParser<IntendedComponents>
{
  public IntendedComponentsParser(String filePath) {
    super(filePath);
  }

  @Override
  protected Class<IntendedComponents> getClassObject()
  {
    return IntendedComponents.class;
  }
}
