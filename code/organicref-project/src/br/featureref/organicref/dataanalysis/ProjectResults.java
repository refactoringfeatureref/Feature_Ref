package br.featureref.organicref.dataanalysis;

import java.util.List;

public class ProjectResults
{
  public final List<ReleaseResults> releaseResults;
  public String name;

  public ProjectResults(final List<ReleaseResults> releaseResults, final String name)
  {
    this.releaseResults = releaseResults;
    this.name = name;
  }
}
