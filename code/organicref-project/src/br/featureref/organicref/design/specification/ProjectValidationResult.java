package br.featureref.organicref.design.specification;

import java.util.ArrayList;
import java.util.List;

import br.featureref.organicref.design.specification.model.DesignRule;
import br.featureref.organicref.design.specification.model.DesignViolation;
import br.featureref.organicref.model.entities.Type;

public class ProjectValidationResult
{
  List<DesignViolation> violations = new ArrayList<>();

  public void addViolation(final Type typeFrom, final Type typeTo, final DesignRule violatedRule)
  {
    violations.add(new DesignViolation(typeFrom, typeTo, violatedRule));
  }
}
