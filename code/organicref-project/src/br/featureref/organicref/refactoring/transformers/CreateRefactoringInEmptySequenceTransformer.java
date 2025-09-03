package br.featureref.organicref.refactoring.transformers;

import static br.featureref.organicref.util.ListUtil.getAndRemoveRandomElementFrom;
import static br.featureref.organicref.util.ListUtil.getRandomElementFrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.text.html.Option;

import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.optimization.solution.RefactoringSolution;
import br.featureref.organicref.optimization.solution.generators.SequenceGenerator;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.operations.ExtractClass;
import br.featureref.organicref.refactoring.operations.InterTypeRefactoring;
import br.featureref.organicref.refactoring.operations.MoveField;
import br.featureref.organicref.refactoring.operations.MoveMethod;
import br.featureref.organicref.refactoring.operations.MoveOperation;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class CreateRefactoringInEmptySequenceTransformer implements RefactoringTransformer
{
	@Override
	public boolean applyTransformation(final RefactoringSolution newSolution, final RefactoringSequence sequence)
	{
		if (sequence.getRefactorings().size() > 0) {
			return false;
		}

		Project project = newSolution.getOriginalProject();
		Type type = project.getTypeByQualifiedName(sequence.getOriginalTypeQualifiedName()).get();
		SequenceGenerator generator = new SequenceGenerator(project);
		List<RefactoringSequence> availableSequences = generator.getAvailableSequences(type);

		if (availableSequences.size() > 0)
		{
			RefactoringSequence<Refactoring> selectedNewSequence = getRandomElementFrom(availableSequences);
			newSolution.replaceVariable(sequence, selectedNewSequence);
			return true;
		}
		return false;
	}
}
