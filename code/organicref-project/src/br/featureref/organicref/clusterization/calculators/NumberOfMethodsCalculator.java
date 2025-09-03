package br.featureref.organicref.clusterization.calculators;

import br.featureref.organicref.model.entities.Type;

public class NumberOfMethodsCalculator {
	
	public static double getValue(Type type) {
		return type.getMethods().size();
	}
}
