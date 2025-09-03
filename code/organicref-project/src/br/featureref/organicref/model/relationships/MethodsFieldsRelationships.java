package br.featureref.organicref.model.relationships;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;

public class MethodsFieldsRelationships {
	
	private Map<String, Set<Field>> methodToFieldsMapping;
	private Map<String, Set<Method>> fieldsToMethodsMapping;
	
	public MethodsFieldsRelationships() {
		this.methodToFieldsMapping = new HashMap<>();
		this.fieldsToMethodsMapping = new HashMap<>();
	}
	
	public void addRelationship(Method method, Field field) {
		Set<Field> fieldSet = methodToFieldsMapping.get(method.getFullyQualifiedNameWithParams());
		if (fieldSet == null) {
			fieldSet = new HashSet<>();
			methodToFieldsMapping.put(method.getFullyQualifiedNameWithParams(), fieldSet);
		}
		fieldSet.add(field);
		
		
		Set<Method> methodsSet = fieldsToMethodsMapping.get(field.getFullyQualifiedName());
		if (methodsSet == null) {
			methodsSet = new HashSet<>();
			fieldsToMethodsMapping.put(field.getFullyQualifiedName(), methodsSet);
		}
		methodsSet.add(method);
	}
	
	public void removeRelationship(Method method, Field field) {
		Set<Field> fieldSet = methodToFieldsMapping.get(method.getFullyQualifiedNameWithParams());
		if (fieldSet != null) {
			fieldSet.remove(field);
		}
		
		Set<Method> methodSet = fieldsToMethodsMapping.get(field.getFullyQualifiedName());
		if (methodSet != null) {
			methodSet.remove(method);
		}
	}
	
	public List<Method> getMethodsThatUse(Field field) {
		return new ArrayList<>(fieldsToMethodsMapping.getOrDefault(field.getFullyQualifiedName(), new HashSet<>()));
	}
	
	public List<Field> getFieldsUsedBy(Method method) {
		return new ArrayList<>(methodToFieldsMapping.getOrDefault(method.getFullyQualifiedNameWithParams(), new HashSet<>()));
	}

  public Collection<Type> getDependenciesOfType(final Type type)
  {
		Set<Type> relatedTypes = new HashSet<>();
		for (Method method : type.getMethods()) {
			relatedTypes.addAll(getFieldsUsedBy(method).stream().map(Field::getParentType).collect(Collectors.toSet()));
		}
		return relatedTypes;
  }

	public Collection<Type> getTypesDependingOn(final Type type)
	{
		Set<Type> relatedTypes = new HashSet<>();
		for (Field field : type.getFields()) {
			relatedTypes.addAll(getMethodsThatUse(field).stream().map(Method::getParentType).collect(Collectors.toSet()));
		}
		return relatedTypes;
	}
}
