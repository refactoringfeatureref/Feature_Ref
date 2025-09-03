package br.featureref.organicref.clusterization.calculators;

import java.util.Set;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.StatementAbstraction;
import br.featureref.organicref.model.entities.StatementKind;
import br.featureref.organicref.model.entities.Type;

public class CyclomaticComplexityCalculator
{
  public static final Set<StatementKind> COMPLEXITY_INCREASING_KINDS = Set.of(StatementKind.SWITCH_CASE,
      StatementKind.FOR_STATEMENT, StatementKind.IF_STATEMENT, StatementKind.DO_STATEMENT, StatementKind.ENHANCEDFOR_STATEMENT
  , StatementKind.WHILE_STATEMENT, StatementKind.TRY_STATEMENT);

  public static double getValue(Type type) {
    return type.getMethods().stream().map(m -> getValue(m)).max(Double::compareTo).orElse(0.0);
  }

  public static double getValue(Method method) {
    return method.getStatements().stream().map(m -> getValue(m)).max(Double::compareTo).orElse(0.0);
  }

  public static double getValue(StatementAbstraction statement) {
    double complexity = 0;
    if (COMPLEXITY_INCREASING_KINDS.contains(statement.getKind())) {
      complexity = 1;
    }
    if (statement.getChildren().size() > 0)
    {
      return complexity + statement.getChildren().stream().map(m -> getValue(m)).max(Double::compareTo).orElse(0.0);
    }
    return complexity;
  }
}
