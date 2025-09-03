package br.featureref.organicref.refactoring.transformers;

import static br.featureref.organicref.util.ListUtil.getAndRemoveRandomElementFrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.operations.ExtractClass;
import br.featureref.organicref.refactoring.operations.MoveField;
import br.featureref.organicref.refactoring.operations.MoveMethod;
import br.featureref.organicref.refactoring.operations.MoveOperation;
import br.featureref.organicref.refactoring.operations.Refactoring;
import br.featureref.organicref.refactoring.operations.InterTypeRefactoring;

public class AddRefactoringToSequenceTransformer implements RefactoringTransformer
{
	@Override
	public boolean applyTransformation(final RefactoringSolution newSolution, final RefactoringSequence sequence)
	{
		final Optional optionalResult = execute(sequence, newSolution.getOriginalProject());

		return optionalResult.isPresent();
	}


	public Optional<RefactoringSequence> execute(RefactoringSequence<Refactoring> sequence, Project project) {
		Type type = project.getTypeByQualifiedName(sequence.getOriginalTypeQualifiedName()).get();

		InterTypeRefactoring newRefactoring = null;
		List<Refactoring> availableRefactorings = new ArrayList<>(sequence.getRefactorings());
		while (newRefactoring == null && availableRefactorings.size() > 0)
		{
			Refactoring selectedRefactoring = getAndRemoveRandomElementFrom(availableRefactorings);
			if (selectedRefactoring instanceof ExtractClass)
			{
				newRefactoring = createNewExtractClass((ExtractClass) selectedRefactoring, type);
				sequence.removeRefactoring(selectedRefactoring);
			}
			else if (selectedRefactoring instanceof MoveMethod)
			{
				newRefactoring = createNewMoveElement((MoveMethod) selectedRefactoring, type, sequence);
			}
		}

		if (newRefactoring != null)
		{
			sequence.addRefactoring(newRefactoring);
			return Optional.of(sequence);
		}

		return Optional.empty();
	}

	private InterTypeRefactoring createNewMoveElement(MoveOperation selectedRefactoring, Type type, RefactoringSequence refactoringSequence) {
		if (selectedRefactoring instanceof MoveMethod) {
			MoveMethod moveMethod = (MoveMethod) selectedRefactoring;
			
			String methodIdentifier = moveMethod.getMethodNameWithParams();
			Method method = type.getMethodByQualifiedNameWithParams(methodIdentifier).get();
			MethodCallRelationships relationships = type.getLocalMethodCallRelationships();
			
			Set<Method> relatedMethods = relationships.getMethodsCalledBy(method);
			Set<Method> dependentMethods = relationships.getMethodsThatDependOn(method);
			List<Field> usedFields = type.getLocalMethodsFieldsRelationships().getFieldsUsedBy(method);
			List<Element> relatedElements = new ArrayList<>(relatedMethods);
			relatedElements.addAll(dependentMethods);
			relatedElements.addAll(usedFields);
			
			List<String> identifiers = getRefactoredIdentifiersOf(refactoringSequence.getRefactorings());
			
			while (relatedElements.size() > 0) {
				Element relatedElement = getAndRemoveRandomElementFrom(relatedElements);
				if (!identifiers.contains(relatedElement.getIdentifier())) {
					if (relatedElement instanceof Method) {
						Method newMovedMethod = (Method) relatedElement;
						if (!newMovedMethod.isConstructor() && !newMovedMethod.isOverride()) {
							return new MoveMethod(newMovedMethod, moveMethod.getTargetTypeQualifiedName());
						}
					} else {
						Field newMovedField = (Field) relatedElement;
						return new MoveField(newMovedField, moveMethod.getTargetTypeQualifiedName());
					}
				}
			}
		} else {
			//TODO implement for move field
		}
		
		return null;
	}

	private List<String> getRefactoredIdentifiersOf(List<InterTypeRefactoring> variables) {
		List<String> identifiers = new ArrayList<>();
		for (InterTypeRefactoring refactoring : variables) {
			identifiers.addAll(refactoring.getRefactoredElementsIdenfiers());
		}
		return identifiers;
	}

	private InterTypeRefactoring createNewExtractClass(ExtractClass extractClass, Type type) {
		List<Element> typeElements = new ArrayList<>(type.getMethods());
		typeElements.addAll(type.getFields());
		
		while (typeElements.size() > 0) {
			Element randomElement = getAndRemoveRandomElementFrom(typeElements);
			String identifier = randomElement.getIdentifier();
			
			if (!extractClass.getAllExtractedElementsNames().contains(identifier)) {
				List<String> extractedFieldsNames = new ArrayList<>(extractClass.getExtractedFieldsNames());
				List<String> extractedMethodsNames = new ArrayList<>(extractClass.getExtractedMethodsNames());
				if (randomElement instanceof Method) {
					extractedMethodsNames.add(identifier);
				} else {
					extractedFieldsNames.add(identifier);
				}
				
				return new ExtractClass(extractedFieldsNames, extractedMethodsNames, type);
			}
		}
		return null;
	}
}
