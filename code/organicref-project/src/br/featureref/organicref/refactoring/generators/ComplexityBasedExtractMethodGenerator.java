package br.featureref.organicref.refactoring.generators;

import static br.featureref.organicref.clusterization.calculators.CyclomaticComplexityCalculator.COMPLEXITY_INCREASING_KINDS;
import static br.featureref.organicref.util.ListUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.StatementAbstraction;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.optimization.solution.variables.MixedOperationsSequence;
import br.featureref.organicref.quality.metrics.MethodMetrics;
import br.featureref.organicref.quality.metrics.ThresholdTypes;
import br.featureref.organicref.refactoring.operations.ExtractMethod;
import br.featureref.organicref.refactoring.operations.Refactoring;
import br.featureref.organicref.refactoring.operations.StatementsExtractionCandidate;

public class ComplexityBasedExtractMethodGenerator
{
  public Optional<MixedOperationsSequence> generate(Project project, Type type) {
    Double threshold = project.getProjectThreshold(ThresholdTypes.CYCLOMATIC_COMPLEXITY_75_PERCENTIL);
    Map<Method, List<StatementsExtractionCandidate>> methodCandidatesMap = generateCandidatesForMethods(type.getMethods(), threshold);

    List<Refactoring> generatedRefactorings = new ArrayList<>();
    for (Map.Entry<Method, List<StatementsExtractionCandidate>> entry : methodCandidatesMap.entrySet()) {
      generatedRefactorings.add(createExtractMethod(type, entry.getKey(), entry.getValue()));
    }

    if (generatedRefactorings.size() > 0) {
      return Optional.of(new MixedOperationsSequence(generatedRefactorings, type.getFullyQualifiedName()));
    }

    return Optional.empty();
  }

  private Refactoring createExtractMethod(Type type, Method method, List<StatementsExtractionCandidate> allCandidates)
  {
    StatementsExtractionCandidate candidate = getRandomElementFrom(allCandidates);
    return new ExtractMethod(type.getFullyQualifiedName(), method.getFullyQualifiedNameWithParams(), candidate.getQualifiedNames(), allCandidates);
  }

  private Map<Method, List<StatementsExtractionCandidate>> generateCandidatesForMethods(final List<Method> methods, final Double threshold)
  {
    Map<Method, List<StatementsExtractionCandidate>> refactoringCandidates = new HashMap<>();
    for (Method method : methods) {
      if (method.getMetric(MethodMetrics.CYCLOMATIC_COMPLEXITY) >= threshold) {
        List<StatementsExtractionCandidate> candidateSet = generateCandidates(method);
        if (candidateSet.size() > 0) {
          refactoringCandidates.put(method, candidateSet);
        }
      }
    }
    return refactoringCandidates;
  }

  private List<StatementsExtractionCandidate> generateCandidates(Method method)
  {
    if (method.getStatements().size()  == 1) {
      StatementAbstraction methodBody = method.getStatements().get(0);
      return extractableStatementsOf(methodBody, false);
    }
    return new ArrayList<>();
  }

  private List<StatementsExtractionCandidate> extractableStatementsOf(StatementAbstraction statement, boolean isInsideComplexityIncreasing)
  {
    List<StatementsExtractionCandidate> candidates  = new ArrayList<>();
    boolean isComplexityIncreasing = COMPLEXITY_INCREASING_KINDS.contains(statement.getKind());
    if (isInsideComplexityIncreasing && isComplexityIncreasing) {
      StatementsExtractionCandidate candidate = new StatementsExtractionCandidate(Arrays.asList(statement.getFullyQualifiedName()));
      candidates.add(candidate);
    }

    for (StatementAbstraction subStatement : statement.getChildren()) {
      candidates.addAll(extractableStatementsOf(subStatement, isInsideComplexityIncreasing || isComplexityIncreasing));
    }
    return candidates;
  }
}
