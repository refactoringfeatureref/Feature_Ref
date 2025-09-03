package br.featureref.organicref.quality.symptoms;

import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;

public class FeatureEnvy extends MethodSymptom {

	private Type enviedType;

	public FeatureEnvy(Method method, Type enviedType) {
		super(method);
		this.enviedType = enviedType;
	}

	public Type getEnviedType()
	{
		return enviedType;
	}

	@Override
	public String toString()
	{
		return "FeatureEnvy{" +
				"enviedType=" + enviedType +
				'}';
	}
}
