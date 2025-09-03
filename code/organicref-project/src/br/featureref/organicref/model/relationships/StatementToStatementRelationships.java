package br.featureref.organicref.model.relationships;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.featureref.organicref.model.entities.StatementAbstraction;

public class StatementToStatementRelationships {
	
	//All statements called by each statement
	private Map<StatementAbstraction, Set<StatementAbstraction>> calledStatementsMapping;
	
	//All statements that depend on each statement
	private Map<StatementAbstraction, Set<StatementAbstraction>> dependentStatementsMapping;
	
	public StatementToStatementRelationships() {
		this.calledStatementsMapping = new HashMap<>();
		this.dependentStatementsMapping = new HashMap<>();
	}
	
	public void addRelationship(StatementAbstraction caller, StatementAbstraction callee) {
		//Do not add a relationship with incorrect parameters
		if (caller == null || callee == null) return;
		
		Set<StatementAbstraction> calledStatementsSet = calledStatementsMapping.get(caller);
		if (calledStatementsSet == null) {
			calledStatementsSet = new HashSet<>();
			calledStatementsMapping.put(caller, calledStatementsSet);
		}
		calledStatementsSet.add(callee);
		
		Set<StatementAbstraction> dependentStatementsSet = dependentStatementsMapping.get(callee);
		if (dependentStatementsSet == null) {
			dependentStatementsSet = new HashSet<>();
			dependentStatementsMapping.put(callee, dependentStatementsSet);
		}
		dependentStatementsSet.add(caller);
	}
	
	public void removeRelationship(StatementAbstraction caller, StatementAbstraction callee) {
		Set<StatementAbstraction> calledStatementsSet = calledStatementsMapping.get(caller);
		if (calledStatementsSet != null) {
			calledStatementsSet.remove(callee);
		}
		
		Set<StatementAbstraction> dependentStatementsSet = dependentStatementsMapping.get(callee);
		if (dependentStatementsSet != null) {
			dependentStatementsSet.remove(caller);
		}
	}
	
	public Set<StatementAbstraction> getStatementsCalledBy(StatementAbstraction statement) {
		return calledStatementsMapping.getOrDefault(statement, new HashSet<>());
	}
	
	public Set<StatementAbstraction> getStatementsThatDependOn(StatementAbstraction statement) {
		return dependentStatementsMapping.getOrDefault(statement, new HashSet<>());
	}
}
