package br.featureref.organicref.context;

import java.util.Collection;

import br.featureref.organicref.context.strategies.ChangedFilesContextDetectionStrategy;
import br.featureref.organicref.context.strategies.TopDegradedTypesContextDetectionStrategy;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;

public interface ContextDetectionStrategy
{
  void createContext(Project originalProject);
}
