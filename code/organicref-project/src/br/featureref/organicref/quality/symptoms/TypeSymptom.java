package br.featureref.organicref.quality.symptoms;

import br.featureref.organicref.model.entities.Type;

public class TypeSymptom {

	private Type type;

	public TypeSymptom(Type type) {
		this.setType(type);
	}


	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}
}
