package br.featureref.organicref.optimization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.featureref.organicref.optimization.solution.RefactoringSolution;

public class SearchResult
{
  private List<RefactoringSolution> population;
  private Map<String, String> information = new HashMap<>();

  public SearchResult(List<RefactoringSolution> population) {
    this.population = population;
  }

  public void setInformation(String key, String value) {
    this.information.put(key, value);
  }

  public Map<String, String> getInformation() {
    return this.information;
  }
}
