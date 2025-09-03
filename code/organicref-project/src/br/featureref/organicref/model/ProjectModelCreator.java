package br.featureref.organicref.model;

import java.io.IOException;
import java.util.Optional;

import br.featureref.organicref.context.Context;
import br.featureref.organicref.context.ContextDetectionStrategy;
import br.featureref.organicref.context.strategies.TopDegradedTypesContextDetectionStrategy;
import br.featureref.organicref.model.builder.ConcernsBuilder;
import br.featureref.organicref.model.builder.ModelFromSourceCodeBuilder;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.quality.ProjectSymptomsDetector;

public class ProjectModelCreator
{
  private final ConcernsBuilder concernsBuilder;
  private String projectPath;
  private ContextDetectionStrategy contextDetectionStrategy;
  private boolean buildConcerns;
  public static final int NUMBER_OF_CONCERNS = 50;

  public ProjectModelCreator(String projectPath, ContextDetectionStrategy contextDetectionStrategy, boolean buildConcerns) {
    this.projectPath = projectPath;
    this.contextDetectionStrategy = contextDetectionStrategy;
    this.buildConcerns = buildConcerns;
    concernsBuilder = new ConcernsBuilder(NUMBER_OF_CONCERNS, true);
  }

  public ProjectModelCreator(final String projectPath)
  {
    this(projectPath, new TopDegradedTypesContextDetectionStrategy(5), true);
  }

  public Optional<Project> create() {
    ModelFromSourceCodeBuilder builder = new ModelFromSourceCodeBuilder(projectPath);
    try
    {
      Project originalProject = builder.buildProject();
      if (buildConcerns)
      {
        concernsBuilder.buildConcernsForProject(originalProject);
      }
      findMeasuresAndSymptoms(originalProject);
      findContext(originalProject);
      //expandContextOfProject(originalProject);
      if (buildConcerns)
      {
        originalProject.inferConcernsForMethodsInContext();
      }
      return Optional.ofNullable(originalProject);
    }
    catch (IOException e)
    {
      System.err.println(e);
    }
    return Optional.empty();
  }

  private void findContext(final Project originalProject)
  {
    contextDetectionStrategy.createContext(originalProject);
  }

  private void expandContextOfProject(final Project project)
  {
    Context context = project.getContext();
    project.getContext().getTypes().forEach(type ->
    {
      context.addToExpandedContext(project.getDependenciesOf(type));
      context.addToExpandedContext(project.getTypesDependingOn(type));
    });
  }

  private void findMeasuresAndSymptoms(final Project originalProject)
  {
    ProjectSymptomsDetector symptomsDetector = new ProjectSymptomsDetector(originalProject);
    symptomsDetector.evaluate();
  }

}
