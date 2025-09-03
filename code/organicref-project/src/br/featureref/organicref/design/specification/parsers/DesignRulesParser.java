package br.featureref.organicref.design.specification.parsers;

import br.featureref.organicref.design.specification.model.DesignRules;

public class DesignRulesParser extends JsonSpecificationParser<DesignRules>
{
  public DesignRulesParser(final String filePath)
  {
    super(filePath);
  }

  @Override
  protected Class<DesignRules> getClassObject()
  {
    return DesignRules.class;
  }
}
