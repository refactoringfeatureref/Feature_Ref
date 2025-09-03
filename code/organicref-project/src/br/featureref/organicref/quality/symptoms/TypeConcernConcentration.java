package br.featureref.organicref.quality.symptoms;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.ElementConcern;
import br.featureref.organicref.model.entities.Type;

public class TypeConcernConcentration extends TypeSymptom {

	private SortedSet<ElementConcern> concerns;

	public TypeConcernConcentration(Type type, SortedSet<ElementConcern> sortedSet) {
		super(type);
		this.concerns = sortedSet;
	}

	public SortedSet<ElementConcern> getConcerns() {
		return concerns;
	}
	
	public int getNumberOfConcerns() {
		return this.concerns.size();
	}
	
	public ElementConcern getPredominantConcern() {
		return this.concerns.first();
	}

	public List<ElementConcern> getNonPredominantConcerns() {
		return this.concerns.stream().skip(1).collect(Collectors.toList());
	}

	@Override
	public String toString()
	{
		return "TypeConcernConcentration{" +
				"numberConcerns=" + concerns.size() +
				'}';
	}
}
