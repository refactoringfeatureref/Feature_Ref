package br.featureref.organicref.clusterization.calculators;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.StatementAbstraction;
import br.featureref.organicref.model.entities.Type;

public class NumberOfStatementsCalculator {
	
	public static double getValue(Type type) {
		double numberOfStatements = .0;
		for (Method method : type.getMethods()) {
			numberOfStatements += getValue(method);
		}
		
		return numberOfStatements;
	}
	
	public static double getValue(Method method) {
		int numberOfStatements = 0;
		for (StatementAbstraction statement : method.getStatements()) {
			numberOfStatements += 1;
			numberOfStatements += getNumberOfSubStatements(statement);
		}
		
		return numberOfStatements;
	}

	private static int getNumberOfSubStatements(StatementAbstraction statement) {
		int numberOfSubStatements = 0;
		for (StatementAbstraction subStatement : statement.getChildren()) {
			numberOfSubStatements += 1;
			numberOfSubStatements += getNumberOfSubStatements(subStatement);
		}
		return numberOfSubStatements;
	}
}
