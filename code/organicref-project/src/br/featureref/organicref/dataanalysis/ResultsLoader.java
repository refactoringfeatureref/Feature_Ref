package br.featureref.organicref.dataanalysis;

import static br.featureref.organicref.optimization.results.dtos.OptimizationResultDTO.RESULTS_FILE_SUFFIX;
import static br.featureref.organicref.optimization.results.dtos.context.ContextImpactSummaryDTO.CONTEXT_FILE_SUFFIX;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.featureref.organicref.optimization.results.dtos.OptimizationResultDTO;
import br.featureref.organicref.optimization.results.dtos.context.ContextImpactSummaryDTO;
import br.featureref.organicref.util.InOutUtil;
import br.featureref.organicref.util.OrganicRefOptions;
import br.featureref.organicref.util.resources.ResultsFilesFinder;

public class ResultsLoader
{
  public static final ReleaseResults.AnalysisType ANALYSIS_TYPE = ReleaseResults.AnalysisType.NO_FILTER;
  private final AnalysisMode mode;
  private ResultsFilesFinder filesFinder;
  public final List<ProjectResults> results = new ArrayList<>();
  private String directoryPath;

  public ResultsLoader(String directoryPath) {
    this.directoryPath = directoryPath;
    this.mode = OrganicRefOptions.getInstance().getAnalysisMode(AnalysisMode.MP);
    filesFinder = new ResultsFilesFinder();
  }

  public void fetchResults()
  {
    if (mode == AnalysisMode.MP) {
      final File[] subDirs = new File(directoryPath).listFiles(File::isDirectory);
      Arrays.stream(subDirs).forEach(d -> fetchProject(d.getAbsolutePath()));
    } else {
      fetchProject(directoryPath);
    }
  }

  private void fetchProject(String dir)
  {
    final List<ReleaseResults> releaseResults = filesFinder.findAll(dir)
        .entrySet()
        .stream()
        .flatMap(e -> parse(e.getKey(), e.getValue()).stream())
        .collect(Collectors.toList());
    this.results.add(new ProjectResults(releaseResults, new File(dir).getName()));
  }

  private List<ReleaseResults> parse(final String releaseName, final Collection<File> files)
  {
    List<ReleaseResults> results = new ArrayList<>();
    var release = new ReleaseResults(releaseName, ANALYSIS_TYPE);
    for (File file : files)
    {
      final String path = file.getAbsolutePath();
      if (path.endsWith(CONTEXT_FILE_SUFFIX)) {
        final Optional<ContextImpactSummaryDTO> dto = InOutUtil.parseFileToDTO(file, ContextImpactSummaryDTO.class);
        if (dto.isPresent()) {
          release.contextImpactSummaries.put(dto.get().executionId, dto.get());
        }
      } else if (path.endsWith(RESULTS_FILE_SUFFIX)) {
        final Optional<OptimizationResultDTO> dto = InOutUtil.parseFileToDTO(file, OptimizationResultDTO.class);
        if (dto.isPresent()) {
          release.optimizationResults.put(dto.get().executionId, dto.get());
        }
      }
    }
    release.processData();
    results.add(release);
    return results;
  }
}
