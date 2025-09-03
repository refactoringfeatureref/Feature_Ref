package br.featureref.organicref.quality.symptoms;

import java.util.List;

import br.featureref.organicref.model.entities.Type;

public class EnvyClass extends TypeSymptom {

	private List<FeatureEnvy> featureEnvies;

	public EnvyClass(Type type, List<FeatureEnvy> featureEnvies) {
		super(type);
		this.featureEnvies = featureEnvies;
	}

	public List<FeatureEnvy> getFeatureEnvies()
	{
		return featureEnvies;
	}

	@Override
	public String toString()
	{
		return "EnvyClass{" +
				"featureEnvies=" + featureEnvies +
				'}';
	}
}
