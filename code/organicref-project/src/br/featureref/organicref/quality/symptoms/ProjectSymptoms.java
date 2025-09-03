package br.featureref.organicref.quality.symptoms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.featureref.organicref.model.entities.Type;

public class ProjectSymptoms {
	
	private List<TypeSymptom> typeSymptoms;
	private Map<Type, List<TypeSymptom>> typesPerSymptom;
	
	public ProjectSymptoms() {
		this.typeSymptoms = new ArrayList<>();
		this.typesPerSymptom = new HashMap<>();
	}
	
	public synchronized void addTypeSymptom(TypeSymptom symptom) {
			typeSymptoms.add(symptom);
			List<TypeSymptom> typeSymptoms = this.typesPerSymptom.get(symptom.getType());
			if (typeSymptoms == null)
			{
				typeSymptoms = new ArrayList<>();
				typesPerSymptom.put(symptom.getType(), typeSymptoms);
			}
			typeSymptoms.add(symptom);
	}
	
	public void addTypeSymptoms(List<? extends TypeSymptom> symptoms) {
		if (symptoms != null) {
			symptoms.stream().forEach(this::addTypeSymptom);
		}
	}
	
	public List<TypeSymptom> getAllTypeSymptoms() {
		return this.typeSymptoms;
	}

  public List<Double> getDensityPerType()
  {
		return typesPerSymptom.entrySet()
				.stream()
				.map(e -> (double) e.getValue().size())
				.collect(Collectors.toList());
	}

	public List<Double> getDiversityPerType()
	{
		return typesPerSymptom.entrySet()
				.stream()
				.map(e -> (double) e.getValue()
						.stream()
						.map(v -> v.getClass())
						.collect(Collectors.toSet())
						.size()
				)
				.collect(Collectors.toList());
	}

  public Map<Type, List<TypeSymptom>> getSymptomsPerType()
  {
		return new HashMap<>(this.typesPerSymptom);
  }

  public Collection<TypeSymptom> getSymptomsOfType(final Type type)
  {
		final List<TypeSymptom> typeSymptoms = this.typesPerSymptom.get(type);
		if (typeSymptoms == null) {
			return new ArrayList<>();
		}
		return typeSymptoms;
  }
}
