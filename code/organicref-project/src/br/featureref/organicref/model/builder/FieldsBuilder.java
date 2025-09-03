package br.featureref.organicref.model.builder;

import static br.featureref.organicref.model.builder.AnnotationBuilder.createAnnotationOccurrences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import br.featureref.organicref.ast.visitors.FieldCollector;
import br.featureref.organicref.model.entities.Field;

public class FieldsBuilder {

	private CompilationUnit compilationUnit;

	public FieldsBuilder(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public List<Field> buildFields(AbstractTypeDeclaration typeDeclaration) {
		List<Field> fields = new ArrayList<>();
		FieldCollector visitor = new FieldCollector();
		typeDeclaration.accept(visitor);
		
		List<VariableDeclarationFragment> fieldsDeclarations = visitor.getNodesCollected();
		for (VariableDeclarationFragment fieldDeclaration : fieldsDeclarations) {
			String fieldName = fieldDeclaration.getName().toString();
			IBinding binding = fieldDeclaration.resolveBinding();
			String fullyQualifiedName = "";
			if (binding != null) {
				IVariableBinding variableBinding = (IVariableBinding) binding;
				String classFqn = variableBinding.getDeclaringClass().getQualifiedName();
				fullyQualifiedName = classFqn + "." + fieldName;
			}

			String kind = getFieldKind(fieldDeclaration);
			String fieldType = getFieldType(fieldDeclaration);

			FieldDeclaration parent = (FieldDeclaration) fieldDeclaration.getParent();
			int modifiers = parent.getModifiers();
			String visibility = getVisibility(modifiers);

			int startLineNumber = BuilderUtil.computeStartLineNumber(compilationUnit, fieldDeclaration);
			int endLineNumber = BuilderUtil.computeEndLineNumber(compilationUnit, fieldDeclaration);

			Field field = new Field(fieldDeclaration, fieldName, fullyQualifiedName, kind, visibility, fieldType,
					startLineNumber, endLineNumber);
			
			createAnnotationOccurrences(field, binding);
			
			fields.add(field);
		}

		return fields;
	}

	protected String getFieldKind(VariableDeclarationFragment declaration) {
		StringBuffer buffer = new StringBuffer();
		FieldDeclaration parent = (FieldDeclaration) declaration.getParent();
		int modifiers = parent.getModifiers();

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

		buffer.append("field");

		return buffer.toString();
	}

	private String getVisibility(int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			return "public";
		}

		if (Modifier.isPrivate(modifiers)) {
			return "private";
		}

		if (Modifier.isProtected(modifiers)) {
			return "protected";
		}

		return "default";
	}

	private String getFieldType(VariableDeclarationFragment declaration) {
		FieldDeclaration parent = (FieldDeclaration) declaration.getParent();
		Type type = parent.getType();
		int extraDimensions = declaration.getExtraDimensions();
		String typeName = type.toString();
		if (extraDimensions > 0) {
			for (int i = 1; i <= extraDimensions; i++) {
				typeName += "[]";
			}
		}
		return typeName;
	}

}
