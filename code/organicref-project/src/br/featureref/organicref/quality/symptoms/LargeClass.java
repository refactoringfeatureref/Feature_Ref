package br.featureref.organicref.quality.symptoms;

import br.featureref.organicref.model.entities.Type;

public class LargeClass extends TypeSymptom {

	private int numOfFields;
	private int numOfMethods;
	private int numStatements;

	public LargeClass(Type type, int numOfFields, int numOfMethods, int numStatements) {
		super(type);
		this.setNumOfFields(numOfFields);
		this.setNumOfMethods(numOfMethods);
		this.setNumStatements(numStatements);
	}

	public int getNumOfFields() {
		return numOfFields;
	}

	public void setNumOfFields(int numOfFields) {
		this.numOfFields = numOfFields;
	}

	public int getNumOfMethods() {
		return numOfMethods;
	}

	public void setNumOfMethods(int numOfMethods) {
		this.numOfMethods = numOfMethods;
	}

	public int getNumStatements() {
		return numStatements;
	}

	public void setNumStatements(int numStatements) {
		this.numStatements = numStatements;
	}

	@Override
	public String toString() {
		return "LargeClass [numOfFields=" + numOfFields + ", numOfMethods=" + numOfMethods + ", numStatements="
				+ numStatements + "]";
	}
}
