package br.featureref.organicref.quality.symptoms;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Concern;
import br.featureref.organicref.model.entities.ElementConcern;
import br.featureref.organicref.model.entities.Type;

public class TypeConcernDispersion extends TypeSymptom {

	private Concern concern;

	public TypeConcernDispersion(Type type, Concern concern) {
		super(type);
		this.concern = concern;
	}

	public Concern getConcern() {
		return concern;
	}

	@Override
	public String toString()
	{
		return "TypeConcernDispersion{" +
				"concern=" + concern +
				'}';
	}
}
