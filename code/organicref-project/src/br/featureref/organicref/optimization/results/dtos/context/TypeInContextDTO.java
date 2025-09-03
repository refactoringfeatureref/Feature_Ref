package br.featureref.organicref.optimization.results.dtos.context;

import java.util.Collection;
import java.util.Map;

import br.featureref.organicref.model.entities.ElementConcern;
import br.featureref.organicref.quality.metrics.TypeMetrics;
import br.featureref.organicref.quality.symptoms.TypeSymptom;

public class TypeInContextDTO
{
  public String name;
  public Collection<ConcernDTO> concerns;
  public Map<TypeMetrics, Double> metrics;
  public Collection<String> symptoms;
}
