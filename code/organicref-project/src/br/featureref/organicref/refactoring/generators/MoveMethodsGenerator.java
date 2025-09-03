package br.featureref.organicref.refactoring.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.featureref.organicref.clusterization.basic.ClassElementsCluster;
import br.featureref.organicref.clusterization.basic.ClassElementsClustersDetector;
import br.featureref.organicref.clusterization.basic.ClassElementsClustersDetectorFactory;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.optimization.solution.variables.MoveOperationsSequence;
import br.featureref.organicref.refactoring.operations.MoveMethod;
import br.featureref.organicref.refactoring.operations.MoveOperation;

public class MoveMethodsGenerator implements RefactoringGenerator<MoveOperationsSequence>
{

  private ClassElementsClustersDetectorFactory clustersDetectorFactory;
  private Project project;

  public MoveMethodsGenerator(ClassElementsClustersDetectorFactory clustersDetectorFactory, Project project)
  {
    this.clustersDetectorFactory = clustersDetectorFactory;
    this.project = project;
  }

  public MoveMethodsGenerator(Project project)
  {
    this(new ClassElementsClustersDetectorFactory(), project);
  }

  public Optional<MoveOperationsSequence> generate(Type type)
  {
    List<MoveOperation> refactorings = new ArrayList<>();

    ClassElementsClustersDetector detector = clustersDetectorFactory.createFor(type);
    detector.findClusters();
    List<ClassElementsCluster> clusters = detector.getSmallerClusters();

    for (ClassElementsCluster cluster : clusters)
    {
      refactorings.addAll(findMoveMethodOportunities(cluster));
    }

    if (refactorings.size() > 0)
    {
      return Optional.of(new MoveOperationsSequence(refactorings, type.getFullyQualifiedName()));
    }
    return Optional.empty();
  }

  private Collection<MoveOperation> findMoveMethodOportunities(ClassElementsCluster cluster)
  {
    List<MoveOperation> refactorings = new ArrayList<>();

    //In this first impl, we'll only generate move methods for cluster without fields
    if (!cluster.hasFields())
    {

      for (Method method : cluster.getMethods())
      {
        List<Field> fields = project.getCrossTypesFieldsUses().getFieldsUsedBy(method);
        Set<Method> calledMethods = project.getCrossTypesMethodCalls().getMethodsCalledBy(method);

        Map<Type, Integer> boundingFactorMap = new HashMap<>();
        for (Field field : fields)
        {
          Integer factor = boundingFactorMap.getOrDefault(field.getParentType(), 0);
          boundingFactorMap.put(field.getParentType(), factor + 1);
        }

        for (Method calledMethod : calledMethods)
        {
          Integer factor = boundingFactorMap.getOrDefault(calledMethod.getParentType(), 0);
          boundingFactorMap.put(calledMethod.getParentType(), factor + 1);
        }

        if (!boundingFactorMap.isEmpty())
        {
          List<String> sortedTypes = getSortedTargetsByBoundingFactor(boundingFactorMap);
          refactorings.add(new MoveMethod(method, sortedTypes));
        }
      }
    }

    return refactorings;
  }

  private List<String> getSortedTargetsByBoundingFactor(final Map<Type, Integer> boundingFactorMap)
  {
    return boundingFactorMap.entrySet()
        .stream()
        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
        .map(e -> e.getKey().getFullyQualifiedName())
        .collect(Collectors.toList());
  }
}
