package br.featureref.organicref.model.builder;

import static br.featureref.organicref.model.builder.AnnotationBuilder.createAnnotationOccurrences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import br.featureref.organicref.ast.visitors.MethodCollector;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.StatementAbstraction;
import br.featureref.organicref.model.entities.StatementKind;
import br.featureref.organicref.model.relationships.StatementToStatementRelationships;

public class MethodsBuilder {

	private CompilationUnit compilationUnit;
	private StatementsBuilder statementsBuilder;

	public MethodsBuilder(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
		this.statementsBuilder = new StatementsBuilder(compilationUnit);
	}

	public List<Method> buildMethods(AbstractTypeDeclaration typeDeclaration, String classFullyQualifiedName) {
		List<Method> methods = new ArrayList<>();
		MethodCollector visitor = new MethodCollector();
		typeDeclaration.accept(visitor);
		List<MethodDeclaration> methodsDeclarations = visitor.getNodesCollected();
		for (MethodDeclaration methodDeclaration : methodsDeclarations) {
			String kind = getMethodKind(methodDeclaration);

			boolean isConstructor = methodDeclaration.isConstructor();

			List<String> parametersTypes = new ArrayList<>();
			for (Object obj : methodDeclaration.parameters()) {
				SingleVariableDeclaration declaration = (SingleVariableDeclaration) obj;
				String varArgs = declaration.isVarargs() ? "..." : "";
				parametersTypes.add(declaration.getType().toString() + varArgs);
			}

			boolean isOverride = false;
			IMethodBinding methodBinding = methodDeclaration.resolveBinding();
			if (methodBinding != null) {
				// TODO find a way to identify overrides without annotation
				for (IAnnotationBinding annotationBinding : methodBinding.getAnnotations()) {
					if (annotationBinding.getName().contains("Override")) {
						isOverride = true;
					}
				}
				
			}

			String genericParamtersStr = "";
			try
			{
				final List typeParameters = methodDeclaration.typeParameters();
				if (typeParameters.size() > 0)
				{
					genericParamtersStr = "<" + typeParameters.stream().map(Object::toString).collect(Collectors.joining(",")).toString() + ">";
					if (!genericParamtersStr.contains("<")) {
						genericParamtersStr = "<" + genericParamtersStr + ">";
					}
				}
			}
			catch (Exception e)
			{
				//No action
			}

			String name = genericParamtersStr + methodDeclaration.getName().toString();
			String fullyQualifiedName = classFullyQualifiedName + "." + name;

			int startLineNumber = BuilderUtil.computeStartLineNumber(compilationUnit, methodDeclaration);
			int endLineNumber = BuilderUtil.computeEndLineNumber(compilationUnit, methodDeclaration);
			
			String javaDoc = methodDeclaration.getJavadoc() != null ? methodDeclaration.getJavadoc().toString() : "";
			
			List<StatementAbstraction> statements = statementsBuilder.buildStatements(methodDeclaration);

			Method method = new Method(methodDeclaration, kind, isConstructor, isOverride, name, fullyQualifiedName,
					parametersTypes, startLineNumber, endLineNumber, statements, javaDoc);
			
			createAnnotationOccurrences(method, methodBinding);

			createLocalRelationships(methodDeclaration, method);

			methods.add(method);
		}

		return methods;
	}

	private void createLocalRelationships(MethodDeclaration methodDeclaration, Method method) {
		StatementToStatementRelationships relationships = new StatementToStatementRelationships();

		HashMap<IVariableBinding, StatementAbstraction> variableStatementMap = new HashMap<>();
		for (StatementAbstraction statementAbstraction : method.getStatements()) {
			includeVariableMapping(variableStatementMap, statementAbstraction);
		}
		
		//TODO

		method.setLocalRelationships(relationships);
	}

	private void includeVariableMapping(HashMap<IVariableBinding, StatementAbstraction> variableStatementMap,
			List<StatementAbstraction> children) {
		for (StatementAbstraction statementAbstraction : children) {
			includeVariableMapping(variableStatementMap, statementAbstraction);
		}
	}

	private void includeVariableMapping(HashMap<IVariableBinding, StatementAbstraction> variableStatementMap,
			StatementAbstraction statementAbstraction) {
		if (statementAbstraction.getKind() == StatementKind.VARIABLEDECLARATION_STATEMENT) {
			VariableDeclarationStatement declaration = (VariableDeclarationStatement) statementAbstraction
					.getOriginalNode();
			for (Object object : declaration.fragments()) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) object;
				IVariableBinding variableBinding = fragment.resolveBinding();
				if (variableBinding != null) {
					variableStatementMap.put(variableBinding, statementAbstraction);
				}
			}
		} else {
			includeVariableMapping(variableStatementMap, statementAbstraction.getChildren());
		}
	}

	public String getMethodKind(MethodDeclaration methodDeclaration) {
		StringBuffer buffer = new StringBuffer();
		int modifiers = methodDeclaration.getModifiers();

		if (Modifier.isPublic(modifiers)) {
			buffer.append("public ");
		}

		if (Modifier.isPrivate(modifiers)) {
			buffer.append("private ");
		}

		if (Modifier.isProtected(modifiers)) {
			buffer.append("protected ");
		}

		if (Modifier.isStatic(modifiers)) {
			buffer.append("static ");
		}

		if (Modifier.isAbstract(modifiers)) {
			buffer.append("abstract ");
		}

		if (Modifier.isFinal(modifiers)) {
			buffer.append("final ");
		}

		buffer.append("method");

		return buffer.toString();
	}

}
