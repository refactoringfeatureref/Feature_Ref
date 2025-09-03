package br.featureref.organicref.model.entities;

import static br.featureref.organicref.util.NamesUtil.splitToSubtokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Optional;

import org.eclipse.jdt.core.dom.Statement;

/**
 * This class represents a source code Statement. A statement may contain other statements
 * and may be inside a method or another statement.
 * 
 *
 *
 */
public class StatementAbstraction extends Observable {

	private transient Statement originalNode;
	private int originalStartLineNumber;
	private int originalEndLineNumber;
	private int originalLength;
	private Method parentMethod;
	private StatementAbstraction parentStatement;

	private StatementKind kind;
	private List<StatementAbstraction> children;
	private String bodyText;

	public StatementAbstraction(Statement originalNode, int originalStartLineNumber, int originalEndLineNumber,
			StatementKind kind, List<StatementAbstraction> children) {
		this.originalNode = originalNode;
		this.originalStartLineNumber = originalStartLineNumber;
		this.originalEndLineNumber = originalEndLineNumber;
		this.originalLength = originalNode.getLength();
		this.kind = kind;
		this.children = new ArrayList<>();
		this.parentMethod = null;
		this.parentStatement = null;
		this.bodyText = originalNode.toString();
		addChildren(children);
	}

	public StatementAbstraction(Statement originalNode, int originalStartLineNumber, int originalEndLineNumber,
			StatementKind kind, List<StatementAbstraction> children, int originalLength, String bodyText) {
		this.originalNode = originalNode;
		this.originalStartLineNumber = originalStartLineNumber;
		this.originalEndLineNumber = originalEndLineNumber;
		this.originalLength = originalLength;
		this.kind = kind;
		this.children = new ArrayList<>();
		this.parentMethod = null;
		this.parentStatement = null;
		this.bodyText = bodyText;
		addChildren(children);
	}

	public Optional<StatementAbstraction> getSubStatementByQualifiedName(final String statementQualifiedName)
	{
		for (StatementAbstraction statement : getChildren()) {
			if (statement.getFullyQualifiedName().equals(statementQualifiedName)) {
				return Optional.of(statement);
			}
			final Optional<StatementAbstraction> optionalSubStatement = statement.getSubStatementByQualifiedName(statementQualifiedName);
			if (optionalSubStatement.isPresent())	{
				return optionalSubStatement;
			}
		}
		return Optional.empty();
	}

	private void addChildren(List<StatementAbstraction> children) {
		for (StatementAbstraction statementAbstraction : children) {
			this.children.add(statementAbstraction);
			statementAbstraction.setParentStatement(this);
		}
	}

	public Statement getOriginalNode() {
		return originalNode;
	}

	public int getOriginalStartLineNumber() {
		return originalStartLineNumber;
	}

	public int getOriginalEndLineNumber() {
		return originalEndLineNumber;
	}

	public int getOriginalLength() {
		return originalLength;
	}

	public StatementKind getKind() {
		return kind;
	}

	public List<StatementAbstraction> getChildren() {
		return children;
	}

	public Method getParentMethod() {
		return parentMethod;
	}

	public void setParentMethod(Method parentMethod) {
		this.parentMethod = parentMethod;
	}

	public StatementAbstraction getParentStatement() {
		return parentStatement;
	}

	public void setParentStatement(StatementAbstraction parentStatement) {
		this.parentStatement = parentStatement;
	}
	
	public String getFullyQualifiedName() {
		if (getParentMethod() != null) {
			return getParentMethod().getFullyQualifiedNameWithParams() + "." + getIdentifier();
		} else if (getParentStatement() != null) {
			return getParentStatement().getFullyQualifiedName() + "." + getIdentifier();
		}
		return getIdentifier();
	}

	private String getIdentifier() {
		return getKind().toString() + ":" + getOriginalStartLineNumber();
	}

	@Override
	public String toString() {
		return "StatementAbstraction [FullyQualifiedName=" + getFullyQualifiedName() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(kind, originalEndLineNumber, originalLength, originalStartLineNumber, parentMethod,
				parentStatement);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatementAbstraction other = (StatementAbstraction) obj;
		return kind == other.kind && originalEndLineNumber == other.originalEndLineNumber
				&& originalLength == other.originalLength && originalStartLineNumber == other.originalStartLineNumber
				&& Objects.equals(parentMethod, other.parentMethod)
				&& Objects.equals(parentStatement, other.parentStatement);
	}

	public List<String> getListOfBasicTokens() {
		return splitToSubtokens(this.bodyText);
		//TODO get variable declarations, literal values inside statement
	}

	public String getBodyText()
	{
		return bodyText;
	}
}
