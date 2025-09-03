package br.featureref.organicref.model.builder;

import static br.featureref.organicref.model.builder.AnnotationBuilder.createAnnotationOccurrences;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.featureref.organicref.ast.visitors.LocalFieldAccessCollector;
import br.featureref.organicref.ast.visitors.LocalMethodCallVisitor;
import br.featureref.organicref.ast.visitors.TypeDeclarationCollector;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;

public class TypesBuilder {

	private File file;
	private CompilationUnit compilationUnit;
	private List<Type> types;
	private MethodsBuilder methodsBuilder;
	private FieldsBuilder fieldsBuilder;

	public TypesBuilder(File file, CompilationUnit compilationUnit) {
		this.file = file;
		this.compilationUnit = compilationUnit;
		this.methodsBuilder = new MethodsBuilder(compilationUnit);
		this.fieldsBuilder = new FieldsBuilder(compilationUnit);
	}

	public List<Type> getTypes() {
		return types;
	}

	public File getFile() {
		return file;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void buildTypes() {
		this.types = new ArrayList<>();
		TypeDeclarationCollector visitor = new TypeDeclarationCollector();
		this.compilationUnit.accept(visitor);
		List<AbstractTypeDeclaration> typeDeclarations = visitor.getNodesCollected();
		Set<String> existingTypesNames = new HashSet<>();
		for (AbstractTypeDeclaration typeDeclaration : typeDeclarations) {
			String kind = getTypeKind(typeDeclaration);
			String fullyQualifiedName = getFullyQualifiedName(typeDeclaration);
			String typeName = typeDeclaration.getName().toString();
			if (fullyQualifiedName.isBlank()) {
				fullyQualifiedName = typeName;
			}
			if (existingTypesNames.contains(fullyQualifiedName)) {
				continue;
			} else {
				existingTypesNames.add(fullyQualifiedName);
			}

			String javaDoc = typeDeclaration.getJavadoc() != null ? typeDeclaration.getJavadoc().toString() : "";

			List<Field> fields = fieldsBuilder.buildFields(typeDeclaration);
			List<Method> methods = methodsBuilder.buildMethods(typeDeclaration, fullyQualifiedName);

			
			int startLineNumber = BuilderUtil.computeStartLineNumber(compilationUnit, typeDeclaration);
			int endLineNumber = BuilderUtil.computeEndLineNumber(compilationUnit, typeDeclaration);
			
			Type type = new Type(file, typeDeclaration, kind, typeName, fullyQualifiedName, fields, methods, startLineNumber, endLineNumber, javaDoc);

			
			createAnnotationOccurrences(type, type.getBinding());
			
			createLocalMethodCallRelationships(typeDeclaration, type);
			createLocalMethodFieldsRelationships(typeDeclaration, type);

			this.types.add(type);
		}
	}

	private String getTypeKind(AbstractTypeDeclaration typeDeclaration) {
		StringBuffer buffer = new StringBuffer();
		int modifiers = typeDeclaration.getModifiers();

		if (Modifier.isPublic(modifiers)) {
			buffer.append("public ");
		}

		if (Modifier.isPrivate(modifiers)) {
			buffer.append("private ");
		}

		if (Modifier.isProtected(modifiers)) {
			buffer.append("protected ");
		}

		if (Modifier.isAbstract(modifiers)) {
			buffer.append("abstract ");
		}

		if (Modifier.isFinal(modifiers)) {
			buffer.append("final ");
		}

		if (typeDeclaration instanceof TypeDeclaration) {
			if (((TypeDeclaration)typeDeclaration).isInterface())
				buffer.append("interface");
			else
				buffer.append("class");
		} else if (typeDeclaration instanceof EnumDeclaration) {
			buffer.append("enum");
		}

		return buffer.toString();
	}

	private String getFullyQualifiedName(AbstractTypeDeclaration typeDeclaration) {
		ITypeBinding binding = typeDeclaration.resolveBinding();
		if (binding != null) {
			String fqn = binding.getQualifiedName();
			if (fqn.contains("<")) {
				fqn = fqn.substring(0, fqn.indexOf("<"));
			}
			return fqn;
		} else if (typeDeclaration.getParent() instanceof CompilationUnit) {
			CompilationUnit parent = (CompilationUnit) typeDeclaration.getParent();
			String packageName = parent.getPackage() != null ? parent.getPackage().getName().getFullyQualifiedName() : "";
			return packageName + "." + typeDeclaration.getName().getFullyQualifiedName();
		} else if (typeDeclaration.getParent() instanceof TypeDeclaration) {
			String parentName = getFullyQualifiedName((AbstractTypeDeclaration) typeDeclaration.getParent());
			return parentName + "." + typeDeclaration.getName().getFullyQualifiedName();
		}

		return "";
	}

	private void createLocalMethodCallRelationships(AbstractTypeDeclaration typeDeclaration, Type type) {
		MethodCallRelationships methodCallRelationships = new MethodCallRelationships();

		HashMap<IMethodBinding, Method> methodsMap = new HashMap<>();
		for (Method method : type.getMethods()) {
			methodsMap.put(method.getBinding(), method);
		}

		for (Method caller : type.getMethods()) {
			LocalMethodCallVisitor collector = new LocalMethodCallVisitor(typeDeclaration);
			MethodDeclaration methodDeclaration = caller.getMethodDeclaration();
			methodDeclaration.accept(collector);
			List<IMethodBinding> collected = collector.getNodesCollected();
			for (IMethodBinding calledMethodBinding : collected) {
				Method callee = methodsMap.get(calledMethodBinding);
				methodCallRelationships.addRelationship(caller, callee);
			}
		}
		type.setMethodCallRelationships(methodCallRelationships);
	}

	private void createLocalMethodFieldsRelationships(AbstractTypeDeclaration typeDeclaration, Type type) {
		MethodsFieldsRelationships methodsFieldsRelationships = new MethodsFieldsRelationships();

		HashMap<FieldDeclaration, Field> fieldsMap = new HashMap<>();
		for (Field field : type.getFields()) {
			fieldsMap.put(field.getFieldDeclaration(), field);
		}

		for (Method method : type.getMethods()) {
			LocalFieldAccessCollector collector = new LocalFieldAccessCollector(fieldsMap.keySet());
			MethodDeclaration methodDeclaration = method.getMethodDeclaration();
			methodDeclaration.accept(collector);
			Set<FieldDeclaration> fieldsOfMethod = new HashSet<>();
			fieldsOfMethod.addAll(collector.getNodesCollected());
			for (FieldDeclaration fieldDeclaration : fieldsOfMethod) {
				Field field = fieldsMap.get(fieldDeclaration);
				methodsFieldsRelationships.addRelationship(method, field);
			}
		}
		type.setMethodsFieldsRelationships(methodsFieldsRelationships);
	}
	
	
}
