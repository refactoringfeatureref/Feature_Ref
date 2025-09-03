package br.featureref.organicref.quality.symptoms.detectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.quality.symptoms.EnvyClass;
import br.featureref.organicref.quality.symptoms.FeatureEnvy;

public class FeatureEnvyDetector
{
  private Project evaluatedProject;
  private MethodCallRelationships crossTypesMethodCalls;
  private List<EnvyClass> symptoms;

  public FeatureEnvyDetector(Project evaluatedProject)
  {
    this.evaluatedProject = evaluatedProject;
    this.crossTypesMethodCalls = evaluatedProject.getCrossTypesMethodCalls();
  }

  public FeatureEnvyDetector detect()
  {
    this.symptoms = new ArrayList<>();
    for (Type type : evaluatedProject.getAllTypes())
    {
      detect(type);
    }

    return this;
  }

  private void detect(final Type type)
  {
    final List<FeatureEnvy> envies = type.getMethods()
        .stream()
        .map(this::detect)
        .filter(s -> s != null)
        .collect(Collectors.toList());
    if (envies.size() > 0)
    {
      symptoms.add(new EnvyClass(type, envies));
    }
  }

  public List<EnvyClass> getSymptoms()
  {
    return this.symptoms;
  }

  private FeatureEnvy detect(Method method)
  {
    if (method.isConstructor())
    {
      return null;
    }

    Map<Type, Long> callsMap = crossTypesMethodCalls.getMethodsCalledBy(method)
        .stream()
        .map(m -> m.getParentType())
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    Optional<Map.Entry<Type, Long>> optionalEnviedType = callsMap.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
        .findFirst();

    if (optionalEnviedType.isPresent())
    {
      Long numberOfCalls = optionalEnviedType.get().getValue();
      MethodCallRelationships localRelationships = method.getParentType().getLocalMethodCallRelationships();
      Set<Method> localCalledMethods = localRelationships.getMethodsCalledBy(method);
      if (numberOfCalls >= localCalledMethods.size())
      {
        return new FeatureEnvy(method, optionalEnviedType.get().getKey());
      }
    }
    return null;
  }
}
