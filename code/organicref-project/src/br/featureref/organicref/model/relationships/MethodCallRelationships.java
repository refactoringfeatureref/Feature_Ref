package br.featureref.organicref.model.relationships;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;

public class MethodCallRelationships {
	
	//All methods called by each method
	private Map<String, Set<Method>> calledMethodsMapping;
	
	//All methods that call each method
	private Map<String, Set<Method>> dependentMethodsMapping;
	
	public MethodCallRelationships() {
		this.calledMethodsMapping = new HashMap<>();
		this.dependentMethodsMapping = new HashMap<>();
	}
	
	public void addRelationship(Method caller, Method callee) {
		//Do not add a relationship with incorrect parameters
		if (caller == null || callee == null) return;
		
		Set<Method> calledMethodsSet = calledMethodsMapping.get(caller.getFullyQualifiedNameWithParams());
		if (calledMethodsSet == null) {
			calledMethodsSet = new HashSet<>();
			calledMethodsMapping.put(caller.getFullyQualifiedNameWithParams(), calledMethodsSet);
		}
		calledMethodsSet.add(callee);
		
		Set<Method> dependentMethodsSet = dependentMethodsMapping.get(callee.getFullyQualifiedNameWithParams());
		if (dependentMethodsSet == null) {
			dependentMethodsSet = new HashSet<>();
			dependentMethodsMapping.put(callee.getFullyQualifiedNameWithParams(), dependentMethodsSet);
		}
		dependentMethodsSet.add(caller);
	}
	
	public void removeRelationship(Method caller, Method callee) {
		Set<Method> calledMethodsSet = calledMethodsMapping.get(caller.getFullyQualifiedNameWithParams());
		if (calledMethodsSet != null) {
			calledMethodsSet.remove(callee);
		}
		
		Set<Method> dependentMethodsSet = dependentMethodsMapping.get(callee.getFullyQualifiedNameWithParams());
		if (dependentMethodsSet != null) {
			dependentMethodsSet.remove(caller);
		}
	}
	
	public Set<Method> getMethodsCalledBy(Method method) {
		return calledMethodsMapping.getOrDefault(method.getFullyQualifiedNameWithParams(), new HashSet<>());
	}
	
	public Set<Method> getMethodsThatDependOn(Method method) {
		return dependentMethodsMapping.getOrDefault(method.getFullyQualifiedNameWithParams(), new HashSet<>());
	}

  public Collection<Type> getDependenciesOfType(final Type type)
  {
		Set<Type> relatedTypes = new HashSet<>();
		for (Method method : type.getMethods()) {
			relatedTypes.addAll(getMethodsCalledBy(method).stream().map(m -> m.getParentType()).collect(Collectors.toSet()));
		}
		return relatedTypes;
  }

	public Collection<Type> getTypesDependingOn(final Type type)
	{
		Set<Type> relatedTypes = new HashSet<>();
		for (Method method : type.getMethods()) {
			relatedTypes.addAll(getMethodsThatDependOn(method).stream().map(m -> m.getParentType()).collect(Collectors.toSet()));
		}
		return relatedTypes;
	}

  public double getNumberOfCallsForType(final Type type)
  {
		double sum = 0.0;
		for (Method method : type.getMethods()) {
			sum = sum + getMethodsCalledBy(method).size();
		}
		return sum;
  }
}
