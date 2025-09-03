package br.featureref.organicref.clusterization.calculators;

import br.featureref.organicref.model.entities.Type;

public class NumberOfFieldsCalculator {

	public static double getValue(Type type) {
		return type.getFields().size();
	}
}