package br.featureref.organicref.design.specification;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.featureref.organicref.design.specification.model.DesignRule;
import br.featureref.organicref.design.specification.model.DesignRules;
import br.featureref.organicref.design.specification.model.IntendedComponent;
import br.featureref.organicref.design.specification.model.IntendedComponents;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;

public class ProjectValidator
{
  //TODO for now we only support one type of rule. Should change this class for including more rules
  private IntendedComponents intendedComponents;
  private DesignRules designRules;
  private Project project;
  private Set<Type> typesToValidate;
  private ProjectValidationResult result;
  private Map<IntendedComponent, Set<Type>> componentTypesMap;

  public ProjectValidator()
  {
    this.result = new ProjectValidationResult();
    this.componentTypesMap = new HashMap<>();
  }

  public ProjectValidationResult getResult()
  {
    mapTypesToComponents();

    for (DesignRule rule : designRules.getRules())
    {
      Optional<IntendedComponent> componentFrom = intendedComponents.getComponentByName(rule.getFrom());
      Optional<IntendedComponent> componentTo = intendedComponents.getComponentByName(rule.getTo());
      if (componentFrom.isEmpty() || componentTo.isEmpty())
      {
        System.err.println(String.format("Invalid specification of components or rules. Please verify. Components: %s and %s.",
            rule.getFrom(), rule.getTo()));
        continue;
      }

      final Set<Type> compFromTypes = componentTypesMap.get(componentFrom.get());
      final Set<Type> compToTypes = componentTypesMap.get(componentTo.get());
      validateDependenciesFrom(rule, compFromTypes, compToTypes);
      validateDependenciesTo(rule, compFromTypes, compToTypes);
    }

    return result;
  }

  private void validateDependenciesTo(final DesignRule rule, final Set<Type> compFromTypes, final Set<Type> compToTypes)
  {
    for (Type type : filterTypesToValidate(compToTypes))
    {
      for (Type dependency : project.getCrossTypesMethodCalls().getTypesDependingOn(type))
      {
        if (compFromTypes.contains(dependency))
        {
          result.addViolation(dependency, type, rule);
        }
      }

      for (Type dependency : project.getCrossTypesFieldsUses().getTypesDependingOn(type))
      {
        if (compFromTypes.contains(dependency))
        {
          result.addViolation(dependency, type, rule);
        }
      }
    }
  }

  private void validateDependenciesFrom(final DesignRule rule, final Set<Type> compFromTypes, final Set<Type> compToTypes)
  {
    for (Type type : filterTypesToValidate(compFromTypes))
    {
        for (Type dependency : project.getCrossTypesMethodCalls().getDependenciesOfType(type))
        {
          if (compToTypes.contains(dependency))
          {
            result.addViolation(type, dependency, rule);
          }
        }

        for (Type dependency : project.getCrossTypesFieldsUses().getDependenciesOfType(type))
        {
          if (compToTypes.contains(dependency))
          {
            result.addViolation(type, dependency, rule);
          }
        }
    }
  }

  private Collection<Type> filterTypesToValidate(final Collection<Type> typesToFilter)
  {
    return typesToFilter.stream().filter(t -> typesToValidate.contains(t)).collect(Collectors.toList());
  }

  private void mapTypesToComponents()
  {
    for (Type type : project.getAllTypes())
    {
      for (IntendedComponent component : intendedComponents.getComponents())
      {
        if (component.containsPathOfType(type))
        {
          Set<Type> typeSet = componentTypesMap.get(component);
          if (typeSet == null)
          {
            typeSet = new HashSet<>();
            componentTypesMap.put(component, typeSet);
          }
          typeSet.add(type);
        }
      }
    }
  }

  public ProjectValidator intendedComponents(final IntendedComponents intendedComponents)
  {
    this.intendedComponents = intendedComponents;
    return this;
  }

  public ProjectValidator designRules(final DesignRules designRules)
  {
    this.designRules = designRules;
    return this;
  }

  public ProjectValidator project(final Project project)
  {
    this.project = project;
    return this;
  }

  public ProjectValidator typesToValidate(final List<Type> typesToValidate)
  {
    this.typesToValidate = Set.copyOf(typesToValidate);
    return this;
  }
}
