package br.featureref.organicref.model.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import br.featureref.organicref.ast.visitors.ExternalFieldAccessCollector;
import br.featureref.organicref.ast.visitors.ExternalMethodCallVisitor;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.InheritanceRelationships;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;
import br.featureref.organicref.util.resources.JavaFilesFinder;
import br.featureref.organicref.util.resources.SourceFilesLoader;

public class ModelFromSourceCodeBuilder {

	private final String sourcePath;

	public ModelFromSourceCodeBuilder(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public Project buildProject() throws IOException {
		List<Type> types = buildAllTypes();
		InheritanceRelationships inheritances = buildInheritances(types);
		MethodCallRelationships crossTypesMethodCalls = buildCrossTypesMethodCalls(types);
		MethodsFieldsRelationships crossTypesFieldsUses = buildCrossTypesFieldsUses(types);

		Project project = new Project(types, inheritances, crossTypesMethodCalls, crossTypesFieldsUses);

		return project;
	}

	private List<Type> buildAllTypes() throws IOException {
		List<Type> allTypes = new ArrayList<>();
		JavaFilesFinder sourceLoader = new JavaFilesFinder(sourcePath);
		SourceFilesLoader compUnitLoader = new SourceFilesLoader(sourceLoader);
		List<TypesBuilder> typeBuilders = compUnitLoader.getTypeBuilders();
		for (TypesBuilder typeBuilder : typeBuilders) {
			typeBuilder.buildTypes();
			for (Type type : typeBuilder.getTypes()) {
				allTypes.add(type);
			}
		}
		return allTypes;
	}

	private InheritanceRelationships buildInheritances(List<Type> types) {
		InheritanceRelationships inheritances = new InheritanceRelationships();
		for (Type type : types) {
			inheritances.registerChild(type);
		}
		return inheritances;
	}

	private MethodsFieldsRelationships buildCrossTypesFieldsUses(List<Type> types) {
		MethodsFieldsRelationships relationships = new MethodsFieldsRelationships();
		Map<IVariableBinding, Field> fieldsMap = getFieldsMapFrom(types);

		for (Type type : types) {
			ITypeBinding typeBinding = type.getBinding();
			if (typeBinding != null) {
				for (Method method : type.getMethods()) {
					ExternalFieldAccessCollector collector = new ExternalFieldAccessCollector(typeBinding);
					method.getMethodDeclaration().accept(collector);
					for (IVariableBinding referenceBinding : collector.getNodesCollected()) {
						Field referencedField = fieldsMap.get(referenceBinding);
						if (referencedField != null) {
							relationships.addRelationship(method, referencedField);
						}
					}
				}
			} else {
				// TODO check when and why this happens
				System.out.println("type with no binding: " + type.getFullyQualifiedName());
			}
		}

		return relationships;
	}

	private Map<IVariableBinding, Field> getFieldsMapFrom(List<Type> types) {
		Map<IVariableBinding, Field> fieldsMap = new HashMap<>();
		for (Type type : types) {
			for (Field field : type.getFields()) {
				IVariableBinding binding = field.getVariableDeclarationFragment().resolveBinding();
				if (binding != null) {
					fieldsMap.put(binding, field);
				}
			}
		}

		return fieldsMap;
	}

	private MethodCallRelationships buildCrossTypesMethodCalls(List<Type> types) {
		MethodCallRelationships relationships = new MethodCallRelationships();
		Map<IMethodBinding, Method> methodsMap = getMethodsMapFrom(types);

		for (Type type : types) {
			ITypeBinding typeBinding = type.getBinding();
			if (typeBinding != null) {
				for (Method method : type.getMethods()) {
					ExternalMethodCallVisitor collector = new ExternalMethodCallVisitor(
							type.getNodeAsTypeDeclaration());
					method.getMethodDeclaration().accept(collector);
					for (IMethodBinding methodBinding : collector.getNodesCollected()) {
						Method calledMethod = methodsMap.get(methodBinding);
						if (calledMethod != null) {
							relationships.addRelationship(method, calledMethod);
						}
					}
				}
			} else {
				// TODO check when and why this happens
				System.out.println("type with no binding: " + type.getFullyQualifiedName());
			}
		}

		return relationships;
	}

	private Map<IMethodBinding, Method> getMethodsMapFrom(List<Type> types) {
		Map<IMethodBinding, Method> methodsMap = new HashMap<>();

		for (Type type : types) {
			for (Method method : type.getMethods()) {
				IMethodBinding binding = method.getBinding();
				if (binding != null) {
					methodsMap.put(binding, method);
				}
			}
		}

		return methodsMap;
	}

}
