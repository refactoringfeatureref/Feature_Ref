package br.featureref.organicref.model.entities;

import static br.featureref.organicref.util.NamesUtil.splitToSubtokens;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import br.featureref.organicref.util.NamesUtil;

/**
 * This class represents a source code field (attribute), which can be a class
 * or instance field.
 * 
 *
 *
 */
public class Field extends Element {

	private String visibility;
	private String fieldType;
	private Type parentType;
	private List<String> tokens = null;

	public Field(VariableDeclarationFragment node, String fieldName, String fullyQualifiedName, String kind,
			String visibility, String fieldType, int startLineNumber, int endLineNumber) {
		super(node, kind, startLineNumber, endLineNumber);
		setName(fieldName);
		setFullyQualifiedName(fullyQualifiedName);
		this.visibility = visibility;
		this.fieldType = fieldType;
	}
	
	public Field(String fieldName, String fullyQualifiedName, String kind, String visibility, String fieldType) {
		super(kind, 0, 0, 0);
		setName(fieldName);
		setFullyQualifiedName(fullyQualifiedName);
		this.visibility = visibility;
		this.fieldType = fieldType;
	}

	public String getVisibility() {
		return this.visibility;
	}

	public FieldDeclaration getFieldDeclaration() {
		VariableDeclarationFragment declaration = getVariableDeclarationFragment();
		FieldDeclaration parent = (FieldDeclaration) declaration.getParent();
		return parent;
	}

	public VariableDeclarationFragment getVariableDeclarationFragment() {
		return (VariableDeclarationFragment) this.getNode();
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public Type getParentType() {
		return parentType;
	}

	public void setParentType(Type parentType) {
		this.parentType = parentType;
		if (parentType != null) {
			String fullyQualifiedName = parentType.getFullyQualifiedName() + "." + getName();
			setFullyQualifiedName(fullyQualifiedName);
		}
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}
	
	@Override
	public List<String> getListOfBasicTokens() {
		//TODO get annotations, literal values
		if (tokens == null)
		{
			tokens = NamesUtil.splitToSubtokens(this.getName());
			tokens.addAll(NamesUtil.splitToSubtokens(this.getFieldType()));

			for (AnnotationOccurrence occurrence : this.getAnnotationOccurrences())
			{
				tokens.addAll(splitToSubtokens(occurrence.getAnnotationName()));
			}
			tokens.add(System.lineSeparator());
		}
		
		return tokens;
	}

	public void setTokens(final List<String> tokens)
	{
		this.tokens = tokens;
	}
}
