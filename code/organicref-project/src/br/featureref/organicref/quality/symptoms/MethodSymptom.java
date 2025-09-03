package br.featureref.organicref.quality.symptoms;

import br.featureref.organicref.model.entities.Method;

public class MethodSymptom {

	private Method method;

	public MethodSymptom(Method method) {
		this.setMethod(method);
	}


	public Method getMethod() {
		return method;
	}


	public void setMethod(Method method) {
		this.method = method;
	}
}
