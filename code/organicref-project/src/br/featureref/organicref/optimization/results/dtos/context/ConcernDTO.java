package br.featureref.organicref.optimization.results.dtos.context;

import br.featureref.organicref.model.entities.Concern;
import br.featureref.organicref.model.entities.ElementConcern;

public class ConcernDTO
{
  public Concern concern;
  public Double probability;

  public ConcernDTO(){}

  public ConcernDTO(final ElementConcern ec)
  {
    concern = ec.getConcern();
    probability = ec.getProbability();
  }
}
