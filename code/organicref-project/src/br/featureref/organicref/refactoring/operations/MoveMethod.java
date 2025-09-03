package br.featureref.organicref.refactoring.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;

public class MoveMethod implements MoveOperation
{
  private List<String> allTargetCandidates;
  private String methodNameWithParams;
  private String originalTypeQualifiedName;
  private String targetTypeQualifiedName;

  public MoveMethod(String methodNameWithParams, String originalTypeQualifiedName,
      String targetTypeQualifiedName, List<String> allTargetCandidates)
  {
    this.methodNameWithParams = methodNameWithParams;
    this.originalTypeQualifiedName = originalTypeQualifiedName;
    this.targetTypeQualifiedName = targetTypeQualifiedName;
    this.allTargetCandidates = allTargetCandidates;
  }

  public MoveMethod(Method method, String targetTypeQualifiedName)
  {
    this.methodNameWithParams = method.getFullyQualifiedNameWithParams();
    this.originalTypeQualifiedName = method.getParentType().getFullyQualifiedName();
    this.targetTypeQualifiedName = targetTypeQualifiedName;
    this.allTargetCandidates = new ArrayList<>();
  }

  public MoveMethod(Method method, Type targetType)
  {
    this(method, targetType.getFullyQualifiedName());
  }

  public MoveMethod(final Method method, final List<String> sortedTypes)
  {
    this(method, sortedTypes.get(0));
    this.allTargetCandidates = sortedTypes;
  }

  @Override
  public void applyTo(Project project)
  {
    Optional<Type> nullableOriginalType = project.getTypeByQualifiedName(originalTypeQualifiedName);
    Optional<Type> nullableTargetType = project.getTypeByQualifiedName(getTargetTypeQualifiedName());
    if (!nullableOriginalType.isEmpty() && !nullableTargetType.isEmpty())
    {
      Type originalType = nullableOriginalType.get();
      Type targetType = nullableTargetType.get();

      Optional<Method> nullableMovedMethod = originalType.getMethodByQualifiedNameWithParams(methodNameWithParams);

      if (!nullableMovedMethod.isEmpty())
      {
        Method movedMethod = nullableMovedMethod.get();
        for (Method dependentMethod : new ArrayList<>(originalType.getLocalMethodCallRelationships().getMethodsThatDependOn(movedMethod)))
        {
          project.getCrossTypesMethodCalls().addRelationship(dependentMethod, movedMethod);
          originalType.getLocalMethodCallRelationships().removeRelationship(dependentMethod, movedMethod);
        }
        targetType.addMethod(movedMethod);
        originalType.removeMethod(movedMethod);

        project.addRefactoredTypesByQualifiedNames(originalTypeQualifiedName);
        project.addRefactoredTypesByQualifiedNames(targetType.getFullyQualifiedName());
      }
    }
  }

  @Override
  public Refactoring copy()
  {
    return new MoveMethod(methodNameWithParams, originalTypeQualifiedName, targetTypeQualifiedName, allTargetCandidates);
  }

  @Override
  public String getChangeSummary()
  {
    return
        "method='" + methodNameWithParams + "', targetType='" + targetTypeQualifiedName + '\'';
  }

  @Override
  public String getName()
  {
    return "Move Method";
  }

  public String getMethodNameWithParams()
  {
    return this.methodNameWithParams;
  }

  @Override
  public String getOriginalTypeQualifiedName()
  {
    return originalTypeQualifiedName;
  }

  public String getTargetTypeQualifiedName()
  {
    return targetTypeQualifiedName;
  }

  @Override
  public List<String> getRefactoredElementsIdenfiers()
  {
    return Arrays.asList(methodNameWithParams);
  }

  public List<String> getAllTargetCandidates()
  {
    return List.copyOf(allTargetCandidates);
  }

  @Override
  public String toString()
  {
    return "MoveMethod{" +
        "allTargetCandidates=" + allTargetCandidates +
        ", methodNameWithParams='" + methodNameWithParams + '\'' +
        ", originalTypeQualifiedName='" + originalTypeQualifiedName + '\'' +
        ", targetTypeQualifiedName='" + targetTypeQualifiedName + '\'' +
        '}';
  }
}
