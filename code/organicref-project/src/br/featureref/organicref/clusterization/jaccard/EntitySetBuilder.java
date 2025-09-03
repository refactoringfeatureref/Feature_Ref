package br.featureref.organicref.clusterization.jaccard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.relationships.MethodCallRelationships;
import br.featureref.organicref.model.relationships.MethodsFieldsRelationships;

public class EntitySetBuilder {

	private List<EntitySet<? extends Element>> entitySets;
	private Type type;

	public EntitySetBuilder(Type type) {
		this.type = type;
		this.entitySets = new ArrayList<>();
	}

	public void build() {
		for (Field field : type.getFields()) {
			buildFor(field);
		}

		for (Method method : type.getMethods()) {
			buildFor(method);
		}
	}

	private void buildFor(Method method) {
		List<String> associatedStrings = new ArrayList<>();
		associatedStrings.add(method.getName());

		MethodCallRelationships methodCallRelations = type.getLocalMethodCallRelationships();
		MethodsFieldsRelationships methodsFieldsRelations = type.getLocalMethodsFieldsRelationships();

		Set<Method> dependOn = methodCallRelations.getMethodsThatDependOn(method);
		for (Method dpMethod : dependOn) {
			associatedStrings.add(dpMethod.getName());
		}

		Set<Method> calledBy = methodCallRelations.getMethodsCalledBy(method);
		for (Method calledMethod : calledBy) {
			associatedStrings.add(calledMethod.getName());
		}

		List<Field> fieldsUsedBy = methodsFieldsRelations.getFieldsUsedBy(method);
		for (Field field : fieldsUsedBy) {
			associatedStrings.add(field.getName());
		}

		EntitySet<Method> es = new EntitySet<Method>(method, associatedStrings, entitySets.size());
		entitySets.add(es);
	}

	private void buildFor(Field field) {
		List<String> associatedStrings = new ArrayList<>();
		associatedStrings.add(field.getName());
		
		MethodsFieldsRelationships methodsFieldsRelations = type.getLocalMethodsFieldsRelationships();
		
		for (Method method : methodsFieldsRelations.getMethodsThatUse(field)) {
			associatedStrings.add(method.getName());
		}
		
		EntitySet<Field> es = new EntitySet<Field>(field, associatedStrings, entitySets.size());
		entitySets.add(es);
	}

	public List<EntitySet<? extends Element>> getEntitySets() {
		return entitySets;
	}
}
