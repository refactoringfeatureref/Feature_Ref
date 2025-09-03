package br.featureref.organicref.optimization.solution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.sequencesolution.SequenceSolution;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.optimization.fitness.NumberOfRefactoringsObjective;
import br.featureref.organicref.optimization.fitness.ObjectiveFunction;
import br.featureref.organicref.optimization.solution.variables.RefactoringSequence;
import br.featureref.organicref.refactoring.operations.Refactoring;

public class RefactoringSolution extends AbstractSolution<RefactoringSequence<Refactoring>> implements SequenceSolution<RefactoringSequence<Refactoring>> {

	private static final long serialVersionUID = -3239987288100774220L;
	public static final String REFACTORED_PROJECT = "refactored_project";
	public static final String ORIGINAL_PROJECT = "original_project";
	public ObjectiveFunction[] objectiveFunctions = ObjectiveFunction.getAllObjectiveFunctions();
	private int generation = 0;
	private AtomicBoolean evaluated = new AtomicBoolean(false);

	public RefactoringSolution(int numberOfVariables, int numberOfObjectives)
	{
		super(numberOfVariables, numberOfObjectives);
	}

	public RefactoringSolution(RefactoringSolution solution) {
		super(solution.getLength(), solution.getNumberOfObjectives());

		for (int i = 0; i < getNumberOfObjectives(); i++) {
			setObjective(i, solution.getObjective(i));
		}

		for (int i = 0; i < getNumberOfVariables(); i++) {
			setVariable(i, solution.getVariable(i).copy());
		}

		for (int i = 0; i < getNumberOfConstraints(); i++) {
			setConstraint(i, solution.getConstraint(i));
		}

		attributes = new HashMap<Object, Object>(solution.attributes);

	}

	@Override
	public Solution<RefactoringSequence<Refactoring>> copy() {
		return new RefactoringSolution(this);
	}

	@Override
	public int getLength() {
		return getNumberOfVariables();
	}
	
	public void setRefactoredProject(Project project) {
		this.setAttribute(REFACTORED_PROJECT, project);
	}
	
	public void setOriginalProject(Project project) {
		this.setAttribute(ORIGINAL_PROJECT, project);
	}

	public void createRefactoredProject() {
		Project refactoredProject = getOriginalProject().copy();
		for (int i = 0; i < getNumberOfVariables(); i++) {
			RefactoringSequence refactoring = this.getVariable(i);
			refactoring.applyTo(refactoredProject);
		}
		refactoredProject.updateConcernsInRefactoredTypes(false);
		refactoredProject.updateAllSymptoms();
		setRefactoredProject(refactoredProject);
	}

	public double getObjectivesAsSingle()
  {
		//TODO Consider using weighted average
    return Arrays.stream(getObjectives()).sum() / getObjectives().length;
  }

	public Project getOriginalProject() {
		return (Project) this.getAttribute(ORIGINAL_PROJECT);
	}
	
	public Project getRefactoredProject() {
		return (Project) this.getAttribute(REFACTORED_PROJECT);
	}

	public void replaceVariable(final RefactoringSequence<Refactoring> oldSequence, final RefactoringSequence<Refactoring> newSequence)
	{
		int index = getVariables().indexOf(oldSequence);
		if (index >= 0) {
			setVariable(index, newSequence);
		}
	}

	public void measureObjectives() {
		if (evaluated.compareAndSet(false, true)) {
			if (getRefactoredProject() == null) {
				createRefactoredProject();
			}
			for (int i = 0; i < getObjectives().length; i++) {
				setObjective(i, objectiveFunctions[i].evaluate(this));
				if (!objectiveFunctions[i].getClass().equals(NumberOfRefactoringsObjective.class))
					setAttribute(objectiveFunctions[i].getObjectiveName(), objectiveFunctions[i].evaluate(getRefactoredProject()));
			}
			setRefactoredProject(null);
		}
	}

	public String getDescription() {
		return getVariables().stream()
				.map(rs -> rs.getDescription())
				.collect(Collectors.joining("|"));
	}

	@Override
	public String toString()
	{
		return "RefactoringSolution{" +
				"fitness=" + Arrays.toString(getObjectives()) +
				", refactorings=" +  getVariables() +
				'}';
	}

	public int getGeneration()
	{
		return this.generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}
}
