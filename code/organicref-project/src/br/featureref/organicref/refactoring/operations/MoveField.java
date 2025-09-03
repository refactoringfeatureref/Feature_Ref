package br.featureref.organicref.refactoring.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;

public class MoveField implements MoveOperation {
	
	private String fieldName;
	private String originalTypeQualifiedName;
	private String targetTypeQualifiedName;

	public MoveField(String fieldName, String originalTypeQualifiedName, String targetTypeQualifiedName) {
		this.fieldName = fieldName;
		this.originalTypeQualifiedName = originalTypeQualifiedName;
		this.targetTypeQualifiedName = targetTypeQualifiedName;
	}

	public MoveField(Field field, Type targetType) {
		this.fieldName = field.getFullyQualifiedName();
		this.originalTypeQualifiedName = field.getParentType().getFullyQualifiedName();
		this.targetTypeQualifiedName = targetType.getFullyQualifiedName();
	}
	
	public MoveField(Field field, String targetTypeQualifiedName) {
		this.fieldName = field.getFullyQualifiedName();
		this.originalTypeQualifiedName = field.getParentType().getFullyQualifiedName();
		this.targetTypeQualifiedName = targetTypeQualifiedName;
	}

	@Override
	public void applyTo(Project project) {
		Optional<Type> nullableOriginalType = project.getTypeByQualifiedName(originalTypeQualifiedName);
		Optional<Type> nullableTargetType = project.getTypeByQualifiedName(getTargetTypeQualifiedName());
		if (!nullableOriginalType.isEmpty() && !nullableTargetType.isEmpty()) {
			Type originalType = nullableOriginalType.get();
			Type targetType = nullableTargetType.get();
			
			Optional<Field> nullableMovedMethod = originalType.getFieldByQualifiedName(fieldName);
			
			if (!nullableMovedMethod.isEmpty()) {
				Field movedField = nullableMovedMethod.get();
				for (Method dependentMethod : new ArrayList<>(originalType.getLocalMethodsFieldsRelationships().getMethodsThatUse(movedField))) {
					project.getCrossTypesFieldsUses().addRelationship(dependentMethod, movedField);
					originalType.getLocalMethodsFieldsRelationships().removeRelationship(dependentMethod, movedField);
				}
				targetType.addField(movedField);
				originalType.removeField(movedField);

				project.addRefactoredTypesByQualifiedNames(originalTypeQualifiedName);
				project.addRefactoredTypesByQualifiedNames(targetType.getFullyQualifiedName());
			}
		}
	}

	@Override
	public Refactoring copy()
	{
		return new MoveField(fieldName, originalTypeQualifiedName, targetTypeQualifiedName);
	}

	@Override
	public String getChangeSummary()
	{
		return "fieldName='" + fieldName + "', targetTypeName='" + targetTypeQualifiedName + '\'';
	}

	public String getMethodNameWithParams() {
		return this.fieldName;
	}

	@Override
	public String getOriginalTypeQualifiedName() {
		return originalTypeQualifiedName;
	}

	public String getTargetTypeQualifiedName() {
		return targetTypeQualifiedName;
	}

	@Override
	public List<String> getRefactoredElementsIdenfiers() {
		return Arrays.asList(fieldName);
	}

	@Override
	public String toString()
	{
		return "MoveField{" +
				"fieldName='" + fieldName + '\'' +
				", originalTypeQualifiedName='" + originalTypeQualifiedName + '\'' +
				", targetTypeQualifiedName='" + targetTypeQualifiedName + '\'' +
				'}';
	}

	@Override
	public String getName()
	{
		return "Move Field";
	}
}
