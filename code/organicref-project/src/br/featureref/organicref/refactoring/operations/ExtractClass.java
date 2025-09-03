package br.featureref.organicref.refactoring.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;

public class ExtractClass implements ExtractTypeOperation {

	private List<String> extractedFieldsNames;
	private List<String> extractedMethodsNames;
	private String originalTypeQualifiedName;

	public ExtractClass(List<String> extractedFieldsNames, List<String> extractedMethodsNames, String originalTypeQualifiedName) {
		this.extractedFieldsNames = extractedFieldsNames;
		this.extractedMethodsNames = extractedMethodsNames;
		this.originalTypeQualifiedName = originalTypeQualifiedName;
	}

	public ExtractClass(List<Element> elements, Type parentType) {
		this.extractedFieldsNames = new ArrayList<>();
		this.extractedMethodsNames = new ArrayList<>();
		
		for (Element element : elements) {
			if (element instanceof Method) {
				extractedMethodsNames.add(element.getIdentifier());
			} else {
				extractedFieldsNames.add(element.getIdentifier());
			}
		}
		
		this.originalTypeQualifiedName = parentType.getFullyQualifiedName();
	}

	public ExtractClass(List<String> extractedFieldsNames, List<String> extractedMethodsNames, Type parentType) {
		this.extractedFieldsNames = extractedFieldsNames;
		this.extractedMethodsNames = extractedMethodsNames;
		this.originalTypeQualifiedName = parentType.getFullyQualifiedName();
	}

	@Override
	public void applyTo(Project project) {
		Optional<Type> nullableType = project.getTypeByQualifiedName(originalTypeQualifiedName);
		
		if (!nullableType.isEmpty()) {
			Type type = nullableType.get();
			List<Field> extractedFields = getExtractedFields(type);
			List<Method> extractedMethods = getExtractedMethods(type);
			Type extractedType = createExtractedType(type, extractedFields, extractedMethods);
			project.addType(extractedType);
			
			
			Field refFieldToExtractedType = addRefFieldToExtractedType(type, extractedType);
			
			updateDependencies(project, type, extractedType, refFieldToExtractedType);
			
			removeExtractedElements(type, extractedFields, extractedMethods);

			project.addRefactoredTypesByQualifiedNames(originalTypeQualifiedName);
			project.addRefactoredTypesByQualifiedNames(extractedType.getFullyQualifiedName());
		}
	}

	@Override
	public Refactoring copy()
	{
		return new ExtractClass(extractedFieldsNames, extractedMethodsNames, originalTypeQualifiedName);
	}

	@Override
	public String getChangeSummary()
	{
		return "extractedFields=" + extractedFieldsNames +
				", extractedMethods=" + extractedMethodsNames;
	}

	public void setExtractedMethods(final List<String> extractedMethodsNames)
	{
		this.extractedMethodsNames = extractedMethodsNames;
	}

	public void setExtractedFields(final List<String> extractedFieldsNames)
	{
		this.extractedFieldsNames = extractedFieldsNames;
	}

	private Field addRefFieldToExtractedType(Type type, Type extractedType) {
		String fieldType = extractedType.getFullyQualifiedName();
		String fieldName = RefactoringUtil.toFieldName(extractedType.getName());
		String fullyQualifiedName = type.getFullyQualifiedName() + "." + fieldName;
		String kind = "private";
		String visibility = kind;
		Field refToExtractedType = new Field(fieldName, fullyQualifiedName, kind, visibility, fieldType);
		type.addField(refToExtractedType);
		return refToExtractedType;
	}

	private Type createExtractedType(Type type, List<Field> extractedFields, List<Method> extractedMethods) {
		//TODO improve name definition + start and end line number
		String extractedTypeName = type.getName() + "Extracted";
		String extractedTypeQualifiedName = type.getFullyQualifiedName() + "Extracted";
		
		Type extractedType = new Type(type.getKind(), extractedTypeName, extractedTypeQualifiedName,
				extractedFields, extractedMethods, 0, 0, 0);
		return extractedType;
	}

	private void removeExtractedElements(Type type, List<Field> extractedFields, List<Method> extractedMethods) {
		for (Field field : extractedFields) {
			type.removeField(field);
		}
		
		for (Method method : extractedMethods) {
			type.removeMethod(method);
		}
	}

	private void updateDependencies(Project project, Type type, Type extractedType, Field refFieldToExtractedType) {
		Map<Method, List<Method>> newExternalMethodCalls = createExtractedMethodCalls(type, extractedType);
		createExternalMethodCalls(project, newExternalMethodCalls, refFieldToExtractedType);
		
		Map<Field, List<Method>> newExternalFieldUses = createExtractedFieldUses(type, extractedType);
		createExternalFieldUses(project, newExternalFieldUses, refFieldToExtractedType);
	}

	private Map<Field, List<Method>> createExtractedFieldUses(Type type, Type extractedType) {
		MethodsFieldsRelationships originalFieldUses = type.getLocalMethodsFieldsRelationships();
		
		Map<Field, List<Method>> newExternalFieldUses = new HashMap<>();
		
		Set<Field> extractedFieldsSet = new HashSet<>(extractedType.getFields());
		Set<Method> extractedMethosSet = new HashSet<>(extractedType.getMethods());
		
		MethodsFieldsRelationships extractedFieldUses = new MethodsFieldsRelationships();
		extractedType.setMethodsFieldsRelationships(extractedFieldUses);
		
		for (Field field : extractedFieldsSet) {
			for (Method methodThatUse : new ArrayList<>(originalFieldUses.getMethodsThatUse(field))) {
				if (extractedMethosSet.contains(methodThatUse)) {
					extractedFieldUses.addRelationship(methodThatUse, field);
				} else {
					List<Method> uses = newExternalFieldUses.get(field);
					if (uses == null) {
						uses = new ArrayList<>();
						newExternalFieldUses.put(field, uses);
					}
					uses.add(methodThatUse);
				}
				originalFieldUses.removeRelationship(methodThatUse, field);
			}
		}
		return newExternalFieldUses;
	}

	private void createExternalFieldUses(Project project, Map<Field, List<Method>> newExternalFieldUses, Field refFieldToExtractedType) {
		for (Entry<Field, List<Method>> entry : newExternalFieldUses.entrySet()) {
			for (Method method : entry.getValue()) {
				project.getCrossTypesFieldsUses().addRelationship(method, entry.getKey());
				if (method.getParentType().equals(refFieldToExtractedType.getParentType())) {
					refFieldToExtractedType.getParentType()
											.getLocalMethodsFieldsRelationships()
											.addRelationship(method, refFieldToExtractedType);
				}
			}
		}
	}

	private Map<Method, List<Method>> createExtractedMethodCalls(Type type, Type extractedType) {
		MethodCallRelationships originalMethodCalls = type.getLocalMethodCallRelationships();
		
		Set<Method> extractedMethosSet = new HashSet<>(extractedType.getMethods());
		Map<Method, List<Method>> newExternalMethodCalls = new HashMap<>();
 		
		MethodCallRelationships extractedMethodCalls = new MethodCallRelationships();
 		extractedType.setMethodCallRelationships(extractedMethodCalls);
		
 		for (Method method : extractedMethosSet) {
			for (Method calledMethod : new ArrayList<>(originalMethodCalls.getMethodsCalledBy(method))) {
				if (extractedMethosSet.contains(calledMethod)) {
					extractedMethodCalls.addRelationship(method, calledMethod);
				} else {
					List<Method> calls = newExternalMethodCalls.get(method);
					if (calls == null) {
						calls = new ArrayList<>();
						newExternalMethodCalls.put(method, calls);
					}
					calls.add(calledMethod);
				}
				originalMethodCalls.removeRelationship(method, calledMethod);
			}
			
			for (Method dependentMethod : new ArrayList<>(originalMethodCalls.getMethodsThatDependOn(method))) {
				if (extractedMethosSet.contains(dependentMethod)) {
					extractedMethodCalls.addRelationship(dependentMethod, method);
				} else {
					List<Method> calls = newExternalMethodCalls.get(dependentMethod);
					if (calls == null) {
						calls = new ArrayList<>();
						newExternalMethodCalls.put(dependentMethod, calls);
					}
					calls.add(method);
				}
				originalMethodCalls.removeRelationship(dependentMethod, method);
			}
		}
		return newExternalMethodCalls;
	}

	private void createExternalMethodCalls(Project project, Map<Method, List<Method>> newExternalMethodCalls, Field refFieldToExtractedType) {
		for (Entry<Method, List<Method>> entry : newExternalMethodCalls.entrySet()) {
			for (Method calledMethod : entry.getValue()) {
				project.getCrossTypesMethodCalls().addRelationship(entry.getKey(), calledMethod);
				
				if (!calledMethod.getParentType().equals(refFieldToExtractedType.getParentType())) {
					refFieldToExtractedType.getParentType()
											.getLocalMethodsFieldsRelationships()
											.addRelationship(entry.getKey(), refFieldToExtractedType);;
				}
			}
		}
	}

	private List<Method> getExtractedMethods(Type type) {
		List<Method> extractedMethods = new ArrayList<>();
		for (String methodName : extractedMethodsNames) {
			Optional<Method> nullableMethod = type.getMethodByQualifiedNameWithParams(methodName);
			if (!nullableMethod.isEmpty()) {
				extractedMethods.add(nullableMethod.get());
			}
		}
		return extractedMethods;
	}

	private List<Field> getExtractedFields(Type type) {
		List<Field> extractedFields = new ArrayList<>();
		for (String fieldName : extractedFieldsNames) {
			Optional<Field> nullableField = type.getFieldByQualifiedName(fieldName);
			if (!nullableField.isEmpty()) {
				extractedFields.add(nullableField.get());
			}
		}
		return extractedFields;
	}

	public List<String> getExtractedFieldsNames() {
		return extractedFieldsNames;
	}

	public List<String> getExtractedMethodsNames() {
		return extractedMethodsNames;
	}

	@Override
	public String getOriginalTypeQualifiedName() {
		return originalTypeQualifiedName;
	}

	public List<String> getAllExtractedElementsNames() {
		ArrayList<String> elements = new ArrayList<>(extractedMethodsNames);
		elements.addAll(extractedFieldsNames);
		return elements;
	}

	@Override
	public List<String> getRefactoredElementsIdenfiers() {
		List<String> identifiers = new ArrayList<>(extractedFieldsNames);
		identifiers.addAll(extractedMethodsNames);
		return identifiers;
	}

	@Override
	public String toString()
	{
		return "ExtractClass{" +
				"extractedFieldsNames=" + extractedFieldsNames +
				", extractedMethodsNames=" + extractedMethodsNames +
				", originalTypeQualifiedName='" + originalTypeQualifiedName + '\'' +
				'}';
	}

	@Override
	public String getName()
	{
		return "Extract Class";
	}
}
