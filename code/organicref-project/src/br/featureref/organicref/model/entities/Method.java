package br.featureref.organicref.model.entities;

import static br.featureref.organicref.util.NamesUtil.splitToSubtokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.featureref.organicref.model.relationships.StatementToStatementRelationships;
import br.featureref.organicref.quality.metrics.MethodMetrics;

/**
 * This class represents a source code method, which can be a class or instance
 * method.
 * 
 *  
 *
 */
public class Method extends Element {

	private List<String> parametersTypes;
	private String fullyQualifiedNameWithParams;
	private boolean isOverride;
	private boolean isConstructor;
	private Type parentType;
	private String javaDoc;
	private Map<MethodMetrics, Double> metrics;
	
	private List<StatementAbstraction> statements;
	private StatementToStatementRelationships localRelationships;
	private List<String> tokens = null;

	public Method(MethodDeclaration node, String kind, boolean isConstructor, boolean isOverride,
			String name, String fullyQualifiedName, List<String> parametersTypes, 
			int startLineNumber, int endLineNumber, List<StatementAbstraction> statements, String javaDoc) {
		super(node, kind, startLineNumber, endLineNumber);
		this.isConstructor = isConstructor;
		this.isOverride = isOverride;
		this.parametersTypes = parametersTypes;
		this.javaDoc = javaDoc;
		this.statements = new ArrayList<>();
		this.metrics = new HashMap<>();
		addStatements(statements);
		setName(name);
		setFullyQualifiedName(fullyQualifiedName);
		updateFullyQualifiedNameWithParams();
	}

	public IMethodBinding getBinding() {
		MethodDeclaration declaration = getMethodDeclaration();
		IMethodBinding binding = declaration.resolveBinding();
		return binding;
	}
	
	public MethodDeclaration getMethodDeclaration() {
		return (MethodDeclaration) this.getNode();
	}

  public void setMetric(final MethodMetrics metric, final Double value)
  {
		this.metrics.put(metric, value);
	}

	public double getMetric(MethodMetrics metric) {
		return this.metrics.get(metric);
	}

	public String getJavaDoc()
	{
		return this.javaDoc;
	}

	private void addStatements(List<StatementAbstraction> statements) {
		for (StatementAbstraction statement : statements) {
			addStatement(statement);
		}
	}

	private void addStatement(StatementAbstraction statement) {
		this.statements.add(statement);
		statement.setParentMethod(this);
	}

	private void updateFullyQualifiedNameWithParams() {
		StringBuilder methodDescription = new StringBuilder();
		methodDescription.append(getFullyQualifiedName());
		methodDescription.append("(");
		for (int i = 0; i < parametersTypes.size(); i++) {
			methodDescription.append(parametersTypes.get(i));
			if (i < parametersTypes.size() - 1) {
				methodDescription.append(", ");
			}
		}
		methodDescription.append(")");
		this.fullyQualifiedNameWithParams = methodDescription.toString();
	}

	public List<String> getParametersTypes() {
		return parametersTypes;
	}

	public String getFullyQualifiedNameWithParams() {
		return this.fullyQualifiedNameWithParams;
	}

	@Override
	public String toString() {
		return this.fullyQualifiedNameWithParams;
	}

	public boolean isOverride() {
		return this.isOverride;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public Type getParentType() {
		return parentType;
	}

	public void setParentType(Type parentType) {
		this.parentType = parentType;
		if (parentType != null) {
			String fullyQualifiedName = parentType.getFullyQualifiedName() + "." + getName();
			setFullyQualifiedName(fullyQualifiedName);
			updateFullyQualifiedNameWithParams();
		}
	}

	public StatementToStatementRelationships getLocalRelationships() {
		return localRelationships;
	}

	public void setLocalRelationships(StatementToStatementRelationships localRelationships) {
		this.localRelationships = localRelationships;
	}

	public List<StatementAbstraction> getStatements() {
		return this.statements;
	}

	@Override
	public List<String> getListOfBasicTokens() {
		//TODO get annotations, called methods, used fields
		if (tokens == null)
		{
			tokens = splitToSubtokens(this.getName());
			tokens.add(System.lineSeparator());

			tokens.addAll(splitToSubtokens(this.javaDoc));
			tokens.add(System.lineSeparator());

			for (AnnotationOccurrence occurrence : this.getAnnotationOccurrences())
			{
				tokens.addAll(splitToSubtokens(occurrence.getAnnotationName()));
			}
			tokens.add(System.lineSeparator());

			//TODO collect params names
			for (String paramType : parametersTypes)
			{
				tokens.addAll(splitToSubtokens(paramType));
			}
			tokens.add(System.lineSeparator());

			for (StatementAbstraction statement : statements)
			{
				tokens.addAll(statement.getListOfBasicTokens());
				tokens.add(System.lineSeparator());
			}
		}
		
		return tokens;
	}

	public void setTokens(final List<String> tokens)
	{
		this.tokens = tokens;
	}
	
	@Override
	public String getIdentifier() {
		return this.getFullyQualifiedNameWithParams();
	}

	public StatementAbstraction getStatementByQualifiedName(String statementQualifiedName) {
		for (StatementAbstraction statementAbstraction : statements) {
			if (statementAbstraction.getFullyQualifiedName().equals(statementQualifiedName)) {
				return statementAbstraction;
			}

			Optional<StatementAbstraction> subStatement = statementAbstraction.getSubStatementByQualifiedName(statementQualifiedName);
			if (subStatement.isPresent()) {
				return subStatement.get();
			}
		}
		return null;
	}
}
