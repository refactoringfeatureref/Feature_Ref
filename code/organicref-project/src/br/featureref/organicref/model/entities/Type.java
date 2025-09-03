package br.featureref.organicref.model.entities;

import static br.featureref.organicref.util.NamesUtil.splitToSubtokens;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;
import br.featureref.organicref.quality.metrics.TypeMetrics;

/**
 * This class represents a source code Type (Class). A type contains methods,
 * fields and their relationships.
 * 
 *  
 *
 */
public class Type extends Element {

	private transient File file;
	private List<Method> methods;
	private List<Field> fields;
	private MethodsFieldsRelationships localMethodsFieldsRelationships;
	private MethodCallRelationships localMethodCallRelationships;
	private Map<TypeMetrics, Double> metrics;
	private String javaDoc;

	public Type(File file, AbstractTypeDeclaration typeDeclaration, String kind, String typeName,
			String fullyQualifiedName, List<Field> fields, List<Method> methods, int startLineNumber, int endLineNumber, String javaDoc) {
		super(typeDeclaration, kind, startLineNumber, endLineNumber);
		this.javaDoc = javaDoc;
		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();
		this.metrics = new HashMap<>();
		this.file = file;
		this.setName(typeName);
		this.setFullyQualifiedName(fullyQualifiedName);
		addAllFields(fields);
		addAllMethods(methods);
	}
	
	public Type(String kind, String typeName, String fullyQualifiedName, List<Field> fields, List<Method> methods, int startLineNumber, int endLineNumber, int length) {
		super(kind, startLineNumber, endLineNumber, length);
		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();
		this.metrics = new HashMap<>();
		this.file = null;
		this.setName(typeName);
		this.setFullyQualifiedName(fullyQualifiedName);
		addAllFields(fields);
		addAllMethods(methods);
	}

  public boolean containsConcern(final Concern concern)
  {
		return getElementConcerns().stream()
				.filter(e -> e.getConcern().equals(concern))
				.count() > 0;
  }

	public File getFile()
	{
		return file;
	}

	private void addAllMethods(List<Method> methods) {
		for (Method method : methods) {
			addMethod(method);
		}
	}

	public void addMethod(Method method) {
		this.methods.add(method);
		method.setParentType(this);
	}

	private void addAllFields(List<Field> fields) {
		for (Field field : fields) {
			addField(field);
		}

	}

	public void addField(Field field) {
		this.fields.add(field);
		field.setParentType(this);
	}

	public AbstractTypeDeclaration getNodeAsTypeDeclaration() {
		return (AbstractTypeDeclaration) getNode();
	}

	public ITypeBinding getBinding() {
		ITypeBinding binding = this.getNodeAsTypeDeclaration().resolveBinding();
		return binding;
	}

	public ITypeBinding getSuperclassBinding() {
		ITypeBinding binding = this.getNodeAsTypeDeclaration().resolveBinding();
		if (binding != null) {
			ITypeBinding superclass = binding.getSuperclass();
			return superclass;
		}
		return null;
	}

	public List<Method> getMethods() {
		return methods;
	}

	public List<Field> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	public void setMetric(TypeMetrics metric, Double value) {
		this.metrics.put(metric, value);
	}

	public double getMetric(TypeMetrics metric) {
		return this.metrics.get(metric);
	}

	public MethodsFieldsRelationships getLocalMethodsFieldsRelationships() {
		return localMethodsFieldsRelationships;
	}

	public MethodCallRelationships getLocalMethodCallRelationships() {
		return localMethodCallRelationships;
	}

	public void setMethodCallRelationships(MethodCallRelationships methodCallRelationships) {
		this.localMethodCallRelationships = methodCallRelationships;
	}

	public void setMethodsFieldsRelationships(MethodsFieldsRelationships methodsFieldsRelationships) {
		this.localMethodsFieldsRelationships = methodsFieldsRelationships;
	}

	public String getAbsoluteFilePath() {
		return file.getAbsolutePath();
	}

	public boolean isInterface() {
		return this.getKind().contains("interface");
	}

	public boolean isEnum() {
		return this.getKind().contains("enum");
	}

	public Optional<Field> getFieldByQualifiedName(String fieldName) {
		for (Field field : fields) {
			if (field.getFullyQualifiedName().equals(fieldName)) {
				return Optional.of(field);
			}
		}
		return Optional.empty();
	}
	
	public Optional<Method> getMethodByQualifiedNameWithParams(String methodName) {
		for (Method method : methods) {
			if (method.getFullyQualifiedNameWithParams().equals(methodName)) {
				return Optional.of(method);
			}
		}
		return Optional.empty();
	}

	public void removeMethod(Method method) {
		methods.remove(method);
	}
	
	public void removeField(Field field) {
		fields.remove(field);
	}
	
	@Override
	public List<String> getListOfBasicTokens() {
		List<String> tokens = new ArrayList<>();
		
		tokens.addAll(splitToSubtokens(getName()));
		tokens.add(System.lineSeparator());
		
		tokens.addAll(splitToSubtokens(this.javaDoc));
		tokens.add(System.lineSeparator());
		
		for (AnnotationOccurrence occurrence : this.getAnnotationOccurrences()) {
			tokens.addAll(splitToSubtokens(occurrence.getAnnotationName()));
			tokens.add(System.lineSeparator());
		}
		
		for (Field field : fields) {
			tokens.addAll(field.getListOfBasicTokens());
			tokens.add(System.lineSeparator());
		}
		
		for (Method method : methods) {
			tokens.addAll(method.getListOfBasicTokens());
			tokens.add(System.lineSeparator());
		}
		
		return tokens;
	}

	public String getJavaDoc()
	{
		return javaDoc;
	}
}
