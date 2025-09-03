package br.featureref.organicref.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.rits.cloning.Cloner;

import br.featureref.organicref.concerns.extraction.topicmodeling.ConcernsInferencer;
import br.featureref.organicref.context.Context;
import br.featureref.organicref.model.entities.Concern;
import br.featureref.organicref.model.entities.ElementConcern;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.StatementAbstraction;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.InheritanceRelationships;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;
import br.featureref.organicref.model.relationships.StatementToStatementRelationships;

public class ProjectCloner {

//	public static Project clone(Project baseProject) {
//		Cloner cloner = new Cloner();
//		cloner.dontCloneInstanceOf(ASTNode.class);
//		cloner.dontCloneInstanceOf(File.class);
//		cloner.dontCloneInstanceOf(ConcernsInferencer.class);
//		Project clone = cloner.deepClone(baseProject);
//
//		return clone;
//	}

	public static Project fastClone(Project baseProject) {
		List<Concern> clonedConcerns = new ArrayList<>();
		Map<Integer, Concern> clonedConcernsMap = new HashMap<>();
		for (Concern concern : baseProject.getConcerns()) {
			clonedConcerns.add(concern);
			clonedConcernsMap.put(concern.getTopicId(), concern);
		}

		Collection<Type> clonedTypes = new ArrayList<>();
		Map<String, Type> clonedTypesMap = new HashMap<>();
		Map<String, Method> allClonedMethods = new HashMap<>();
		Set<Field> allClonedFields = new HashSet<>();

		for(Type originalType : baseProject.getAllTypes()) {
			final Type clonedType = fastClone(originalType, clonedConcernsMap);
			clonedTypes.add(clonedType);
			clonedTypesMap.put(clonedType.getFullyQualifiedName(), clonedType);

			for (Method m : clonedType.getMethods()) {
				if (allClonedMethods.containsKey(m.getFullyQualifiedNameWithParams())) {
					System.out.println("Duplicated method id: " + m.getFullyQualifiedNameWithParams());
				}
				allClonedMethods.put(m.getFullyQualifiedNameWithParams(), m);
			}

			for (Field f : clonedType.getFields()) {
				allClonedFields.add(f);
			}
		}

		MethodsFieldsRelationships clonedFieldsRelationships = getClonedFieldsRelationships(baseProject.getCrossTypesFieldsUses(), new ArrayList<>(allClonedFields),
				allClonedMethods);

		MethodCallRelationships clonedMethodsRelationships = getClonedMethodsRelationships(baseProject.getCrossTypesMethodCalls(),
				new ArrayList<>(allClonedMethods.values()), allClonedMethods);

		InheritanceRelationships clonedInheritances = new InheritanceRelationships();
		for (Type type : clonedTypes) {
			clonedInheritances.registerChild(type);
		}

		Project clone = new Project(clonedTypes, clonedInheritances, clonedMethodsRelationships, clonedFieldsRelationships);
		clone.setConcernsInferencer(baseProject.getConcernsInferencer());
		clone.setConcerns(clonedConcerns);

		final List<Type> contextTypes = baseProject.getContext()
				.getTypes()
				.stream()
				.map(t -> clonedTypesMap.get(t.getFullyQualifiedName()))
				.collect(Collectors.toList());

		final List<Type> expandedContextTypes = baseProject.getContext()
				.getTypesInExpandedContext()
				.stream()
				.map(t -> clonedTypesMap.get(t.getFullyQualifiedName()))
				.collect(Collectors.toList());
		Context clonedContext = new Context();
		clonedContext.add(contextTypes);
		clonedContext.addToExpandedContext(expandedContextTypes);
		clone.setContext(clonedContext);

		return clone;
	}

	private static Type fastClone(final Type originalType, Map<Integer, Concern> clonedConcernsMap)
	{
		List<Field> clonedFields = new ArrayList<>();
		for (Field originalField : originalType.getFields()) {
			final Field clonedField = fastClone(originalField);
			clonedFields.add(clonedField);
		}

		List<Method> clonedMethods = new ArrayList<>();
		Map<String, Method> clonedMethodsMap = new HashMap<>();
		for (Method originalMethod : originalType.getMethods()) {
			Method clonedMethod = fastClone(originalMethod);
			clonedMethods.add(clonedMethod);
			clonedMethodsMap.put(clonedMethod.getFullyQualifiedNameWithParams(), clonedMethod);
		}

		Type clonedType = new Type(originalType.getFile(), originalType.getNodeAsTypeDeclaration(),
				originalType.getKind(), originalType.getName(), originalType.getFullyQualifiedName(),
				clonedFields, clonedMethods, originalType.getStartLineNumber(), originalType.getEndLineNumber(), originalType.getJavaDoc());

		for(ElementConcern elementConcern : originalType.getElementConcerns()) {
			final Concern concern = clonedConcernsMap.get(elementConcern.getConcern().getTopicId());
			clonedType.addElementConcern(new ElementConcern(concern, elementConcern.getProbability()));
		}

		MethodsFieldsRelationships clonedFieldsRelationships = getClonedFieldsRelationships(originalType.getLocalMethodsFieldsRelationships(),
				clonedFields, clonedMethodsMap);
		clonedType.setMethodsFieldsRelationships(clonedFieldsRelationships);

		MethodCallRelationships clonedMethodsRelationships = getClonedMethodsRelationships(originalType.getLocalMethodCallRelationships(), clonedMethods,
				clonedMethodsMap);
		clonedType.setMethodCallRelationships(clonedMethodsRelationships);
		clonedType.setTextualRepresentation(originalType.getTextualRepresentation());

		return clonedType;
	}

	private static MethodCallRelationships getClonedMethodsRelationships(final MethodCallRelationships originalMethodsRelationships, final List<Method> clonedMethods,
			final Map<String, Method> clonedMethodsMap)
	{
		MethodCallRelationships clonedMethodsRelationships = new MethodCallRelationships();
		for (Method clonedMethod : clonedMethods) {
			final Set<Method> originalMethods = originalMethodsRelationships.getMethodsCalledBy(clonedMethod);
			originalMethods.stream()
					.map(m -> clonedMethodsMap.get(m.getFullyQualifiedNameWithParams()))
					.forEach(calledMethod -> clonedMethodsRelationships.addRelationship(clonedMethod, calledMethod));
		}
		return clonedMethodsRelationships;
	}

	private static MethodsFieldsRelationships getClonedFieldsRelationships(final MethodsFieldsRelationships originalFieldsRelationships, final List<Field> clonedFields,
			final Map<String, Method> clonedMethodsMap)
	{
		MethodsFieldsRelationships clonedFieldsRelationships = new MethodsFieldsRelationships();
		for (Field clonedField : clonedFields) {
			final List<Method> originalMethods = originalFieldsRelationships.getMethodsThatUse(clonedField);
			originalMethods.stream()
					.map(m -> clonedMethodsMap.get(m.getFullyQualifiedNameWithParams()))
					.forEach(clonedMethod -> clonedFieldsRelationships.addRelationship(clonedMethod, clonedField));
		}
		return clonedFieldsRelationships;
	}

	private static Method fastClone(final Method originalMethod)
	{
		final List<StatementAbstraction> clonedStatements = new ArrayList<>();
		for (StatementAbstraction sa : originalMethod.getStatements()) {
			clonedStatements.add(fastClone(sa));
		}

		Method clonedMethod = new Method((MethodDeclaration)originalMethod.getNode(), originalMethod.getKind(),
				originalMethod.isConstructor(), originalMethod.isOverride(), originalMethod.getName(), originalMethod.getFullyQualifiedName(),
				originalMethod.getParametersTypes(), originalMethod.getStartLineNumber(), originalMethod.getEndLineNumber(), clonedStatements,
				originalMethod.getJavaDoc());

		StatementToStatementRelationships clonedRelations = new StatementToStatementRelationships();
		//TODO finish after completing the implementation
		clonedMethod.setLocalRelationships(clonedRelations);
		clonedMethod.setTokens(originalMethod.getListOfBasicTokens());

		return clonedMethod;
	}

	private static StatementAbstraction fastClone(final StatementAbstraction sa)
	{
		final List<StatementAbstraction> children = new ArrayList<>();
		for (StatementAbstraction subSA : sa.getChildren()) {
			children.add(fastClone(subSA));
		}
		return new StatementAbstraction(sa.getOriginalNode(), sa.getOriginalStartLineNumber(), sa.getOriginalEndLineNumber(),
				sa.getKind(), children, sa.getOriginalLength(), sa.getBodyText());
	}

	private static Field fastClone(final Field originalField)
	{
		final Field clonedField = new Field((VariableDeclarationFragment) originalField.getNode(), originalField.getName(),
				originalField.getFullyQualifiedName(), originalField.getKind(), originalField.getVisibility(),
				originalField.getFieldType(), originalField.getStartLineNumber(), originalField.getEndLineNumber());

		clonedField.setTokens(originalField.getListOfBasicTokens());

		return clonedField;
	}
}
