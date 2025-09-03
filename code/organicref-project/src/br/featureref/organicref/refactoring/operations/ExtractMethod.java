package br.featureref.organicref.refactoring.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.StatementAbstraction;
import br.featureref.organicref.model.entities.StatementKind;
import br.featureref.organicref.model.entities.Type;

public class ExtractMethod implements IntraTypeRefactoring
{
  private String originalTypeQualifiedName;
  private String methodQualifiedNameWithParams;
  private List<String> extractedStatementsIdenfiers;
  private List<StatementsExtractionCandidate> allCandidates;

  public ExtractMethod(final String originalTypeQualifiedName, final String methodQualifiedNameWithParams,
      final List<String> extractedStatementsIdenfiers,
      final List<StatementsExtractionCandidate> allCandidates)
  {
    this.originalTypeQualifiedName = originalTypeQualifiedName;
    this.methodQualifiedNameWithParams = methodQualifiedNameWithParams;
    this.extractedStatementsIdenfiers = extractedStatementsIdenfiers;
    this.allCandidates = allCandidates;
  }

  @Override
  public void applyTo(final Project project)
  {
    Optional<Type> optionalType = project.getTypeByQualifiedName(originalTypeQualifiedName);
    if (optionalType.isPresent()) {
      Type type = optionalType.get();
      Optional<Method> optionalMethod = type.getMethodByQualifiedNameWithParams(methodQualifiedNameWithParams);
      if (optionalMethod.isPresent()) {
        Method method = optionalMethod.get();
        List<StatementAbstraction> statements = extractedStatementsIdenfiers.stream()
            .map(s -> method.getStatementByQualifiedName(s))
            .filter(s -> s != null)
            .collect(Collectors.toList());

        String name = method.getName() + "Extracted";
        String fullyQualifiedName = method.getFullyQualifiedName() + "Extracted";

        StatementAbstraction body = new StatementAbstraction(null, 0
            , 0, StatementKind.BLOCK, statements);

        //TODO define parameters based on dependencies
        List<String> parameters = new ArrayList<>();
        Method extractedMethod = new Method(null, "private method", false, false, name, fullyQualifiedName
        , parameters, 0, 0, Arrays.asList(body), "");

        type.addMethod(extractedMethod);

        //StatementAbstraction methodCallStatement = new StatementAbstraction(null, 0, 0,
        //    StatementKind.EXPRESSION_STATEMENT);

       // method.replaceStatements(statements);


      }
    }
  }

  @Override
  public Refactoring copy()
  {
    return new ExtractMethod(originalTypeQualifiedName, methodQualifiedNameWithParams, new ArrayList<>(extractedStatementsIdenfiers), allCandidates);
  }

  @Override
  public String getChangeSummary()
  {
    return "";
  }

  @Override
  public String getName()
  {
    return "Extract Method";
  }
}
