package br.featureref.organicref.design.specification;

import java.util.List;

import br.featureref.organicref.design.specification.parsers.DesignRulesParser;
import br.featureref.organicref.design.specification.parsers.IntendedComponentsParser;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;

public class DesignSpecificationValidator
{
  private String projectRootPath;
  private DesignRulesParser designRulesParser = null;
  private IntendedComponentsParser intendedComponentsParser = null;

  public DesignSpecificationValidator(String projectRootPath)
  {
    this.projectRootPath = projectRootPath;
  }

  public ProjectValidationResult validateProject(Project project, List<Type> typesToValidate) {
    loadParsers();
    return new ProjectValidator()
        .intendedComponents(intendedComponentsParser.getSpecification())
        .designRules(designRulesParser.getSpecification())
        .project(project)
        .typesToValidate(typesToValidate)
        .getResult();
  }

  private void loadParsers()
  {
    intendedComponentsParser = new IntendedComponentsParser(getIntendedComponentsSpecificationPath());
    designRulesParser = new DesignRulesParser(getDesignRulesParserPath());
  }

  private String getIntendedComponentsSpecificationPath()
  {
    return String.format("%s//intended-components.json", projectRootPath);
  }

  private String getDesignRulesParserPath()
  {
    return String.format("%s//design-rules.json", projectRootPath);
  }
}
