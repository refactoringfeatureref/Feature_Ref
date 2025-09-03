package br.featureref.organicref.model.entities;

import static br.featureref.organicref.model.builder.ConcernsBuilder.buildMethodsConcerns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.featureref.organicref.concerns.extraction.topicmodeling.ConcernsInferencer;
import br.featureref.organicref.context.Context;
import br.featureref.organicref.model.ProjectCloner;
import br.featureref.organicref.model.exceptions.InvalidStateException;
import br.featureref.organicref.model.relationships.InheritanceRelationships;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;
import br.featureref.organicref.quality.ProjectSymptomsDetector;
import br.featureref.organicref.quality.metrics.ThresholdTypes;
import br.featureref.organicref.quality.symptoms.ProjectSymptoms;

/**
 * This class represents a Java project. A project groups code elements (types,
 * methods, and fields) and their corresponding relationships.
 * 
 * 
 *
 */
public class Project {

	private Set<Type> alltypes;
	private Context context;

	private InheritanceRelationships inheritanceRelations;
	private MethodCallRelationships crossTypesMethodCalls;
	private MethodsFieldsRelationships crossTypesFieldsUses;

	private ProjectSymptoms projectSymptoms;

	private ConcernsInferencer concernsInferencer;

	private List<Concern> concerns;
	private Set<Type> typesChangedByRefactorings;

	private Map<ThresholdTypes, Double> projectThresholds;

	public Project(Collection<Type> types, InheritanceRelationships inheritanceRelations,
      MethodCallRelationships crossTypesMethodCalls, MethodsFieldsRelationships crossTypesFieldsUses) {
		this.inheritanceRelations = inheritanceRelations;
		this.crossTypesMethodCalls = crossTypesMethodCalls;
		this.crossTypesFieldsUses = crossTypesFieldsUses;
		this.alltypes = new HashSet<>(types);
		this.concerns = new ArrayList<>();
		this.typesChangedByRefactorings = new HashSet<>();
		this.projectThresholds = new HashMap<>();
	}
	
	public Set<Type> getAllTypes() {
		return this.alltypes;
	}

	public void setSymptoms(ProjectSymptoms projectSymptoms) {
		this.projectSymptoms = projectSymptoms;
	}

	public ProjectSymptoms getProjectSymptoms() {
		return this.projectSymptoms;
	}

	public InheritanceRelationships getInheritanceRelations() {
		return inheritanceRelations;
	}

	public MethodCallRelationships getCrossTypesMethodCalls() {
		return crossTypesMethodCalls;
	}

	public MethodsFieldsRelationships getCrossTypesFieldsUses() {
		return crossTypesFieldsUses;
	}

	public Optional<Type> getTypeByQualifiedName(String parentTypeQualifiedName) {
		//TODO create a map for name->type
		for (Type type : alltypes) {
			if (type.getFullyQualifiedName().equals(parentTypeQualifiedName)) {
				return Optional.of(type);
			}
		}
		
		return Optional.empty();
	}

	public void addType(Type type) {
		this.alltypes.add(type);
	}
	
	public List<String> getTypesAsDocumentsForConcerns() {
		List<String> documents = new ArrayList<>();
		
		for (Type type : alltypes) {
			documents.add(type.getTextualRepresentation());
		}
		
		return documents;
	}

	public void setConcernsInferencer(ConcernsInferencer concernsInferencer) {
		this.concernsInferencer = concernsInferencer;
		if (concernsInferencer != null) {
			this.concerns = concernsInferencer.getAllConcerns();
		}
	}
	
	public ConcernsInferencer getConcernsInferencer() {
		return this.concernsInferencer;
	}
	
	public List<Concern> getConcerns() {
		return this.concerns;
	}

	public void clearAllConcerns() {
		this.concerns = new ArrayList<>();
		for (Type type : alltypes) {
			type.clearElementConcerns();
		}
	}

	public Context getContext()
	{
		return context;
	}

	public void setContext(final Context context)
	{
		this.context = context;
	}

	public Set<Type> getDependenciesOf(final Type type)
	{
		Set<Type> dependenciesOfType = new HashSet<>(getCrossTypesFieldsUses().getDependenciesOfType(type));
		dependenciesOfType.addAll(getCrossTypesMethodCalls().getDependenciesOfType(type));
		return dependenciesOfType;
	}

	public Set<Type> getTypesDependingOn(final Type type)
	{
		Set<Type> typesDependingOn = new HashSet<>(getCrossTypesFieldsUses().getTypesDependingOn(type));
		typesDependingOn.addAll(getCrossTypesMethodCalls().getTypesDependingOn(type));
		return typesDependingOn;
	}

	public void updateConcernsInRefactoredTypes() {
		this.updateConcernsInRefactoredTypes(true);
	}

	public void updateConcernsInRefactoredTypes(boolean updateMethodConcerns) {
		for (Type type : this.getTypesChangedByRefactorings()) {
			type.clearElementConcerns();
			type.clearTextualRepresentation();
			Map<Concern, Double> map = concernsInferencer.inferConcernsForElement(type.getTextualRepresentation());
			for (Map.Entry<Concern, Double> entry : map.entrySet()) {
				type.addElementConcern(new ElementConcern(entry.getKey(), entry.getValue()));
			}

			if (updateMethodConcerns && type.getElementConcerns().size() > 0) {
				updateMethodConcernsOfType(type);
			}
		}
	}

	public void addRefactoredTypesByQualifiedNames(String typeQualifiedName)
	{
		final Optional<Type> optionalType = getTypeByQualifiedName(typeQualifiedName);
		if (!optionalType.isEmpty())
		{
			typesChangedByRefactorings.add(optionalType.get());
		}
	}

	public void updateAllSymptoms()
	{
		ProjectSymptomsDetector symptomsDetector = new ProjectSymptomsDetector(this);
		symptomsDetector.evaluate();
	}

  public Collection<Type> getAllClasses()
  {
		return getAllTypes().stream()
				.filter(t -> !t.isEnum() && !t.isInterface())
				.collect(Collectors.toSet());
  }

  public Set<Type> getTypesOfConcern(final Concern concern)
  {
		return getAllClasses().stream()
				.filter(c -> c.containsConcern(concern))
				.collect(Collectors.toSet());
  }

	public void setConcerns(final List<Concern> clonedConcerns)
	{
		this.concerns = clonedConcerns;
	}

	public void inferConcernsForMethodsInContext()
	{
		if (this.getContext() == null || this.getConcernsInferencer() == null) {
			throw new InvalidStateException("Project should be setup before performing this operation.");
		}

		this.getContext()
				.getAllTypes()
				.forEach(t -> buildMethodsConcerns(this.getConcernsInferencer(), t));
	}

	private void updateMethodConcernsOfType(final Type type)
	{
		for (Element method : type.getMethods()) {
			method.clearElementConcerns();
			Map<Concern, Double> map = concernsInferencer.inferConcernsForElement(method.getTextualRepresentation());
			for (Map.Entry<Concern, Double> entry : map.entrySet()) {
				method.addElementConcern(new ElementConcern(entry.getKey(), entry.getValue()));
			}

			if (method.getElementConcerns().size() == 0) {
				ElementConcern typeConcern = type.getElementConcerns().first();
				method.addElementConcern(new ElementConcern(typeConcern.getConcern(), typeConcern.getProbability()));
			}
		}
	}

	public Collection<Type> getTypesChangedByRefactorings()
	{
		return this.typesChangedByRefactorings;
	}

	public void clearRefactoredTypes()
	{
		this.typesChangedByRefactorings = new HashSet<>();
	}

	public Project copy()
	{
		Project copyOfThis = ProjectCloner.fastClone(this);
		return copyOfThis;
	}

	public void setProjectThreshold(ThresholdTypes type, Double value) {
		this.projectThresholds.put(type, value);
	}

	public Double getProjectThreshold(ThresholdTypes type) {
		return this.projectThresholds.get(type);
	}
}
