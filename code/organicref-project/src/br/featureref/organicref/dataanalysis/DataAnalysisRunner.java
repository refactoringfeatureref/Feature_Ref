package br.featureref.organicref.dataanalysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

import br.featureref.organicref.optimization.results.dtos.SolutionDTO;
import br.featureref.organicref.util.InOutUtil;

public class DataAnalysisRunner
{
  private ResultsLoader loader;

  public DataAnalysisRunner(final String sourcePath)
  {
    loader = new ResultsLoader(sourcePath);
  }

  public void run()
  {
    loader.fetchResults();

    Map<String, Map<String, StrategyStats>> allStats = new HashMap<>();
    for (ProjectResults projectResult : loader.results)
    {
      Map<String, StrategyStats> projectStats = new HashMap<>();
      allStats.put(projectResult.name, projectStats);
      for (ReleaseResults releaseResult : projectResult.releaseResults)
      {
        saveNonDominatedSolutions(releaseResult.releaseName, projectResult.name, releaseResult.nonDominatedPerAlgorithm);
        for (var entry : releaseResult.strategyStatsMap.entrySet())
        {
          var stats = projectStats.get(entry.getKey());
          if (stats == null)
          {
            stats = new StrategyStats();
            stats.strategyName = entry.getKey();
            projectStats.put(entry.getKey(), stats);
          }
          Arrays.stream(entry.getValue().computingTimeStats.getValues())
              .forEach(stats.computingTimeStats::addValue);
          Arrays.stream(entry.getValue().euclideanDistanceStats.getValues())
              .forEach(stats.euclideanDistanceStats::addValue);
          Arrays.stream(entry.getValue().numberRefactoringsStats.getValues())
              .forEach(stats.numberRefactoringsStats::addValue);
          Arrays.stream(entry.getValue().densityStats.getValues())
              .forEach(stats.densityStats::addValue);
          Arrays.stream(entry.getValue().lcomSolutionStats.getValues())
              .forEach(stats.lcomSolutionStats::addValue);
          Arrays.stream(entry.getValue().qmeasuresStats.getValues())
              .forEach(stats.qmeasuresStats::addValue);
          Arrays.stream(entry.getValue().nfeatureStats.getValues())
              .forEach(stats.nfeatureStats::addValue);

          Arrays.stream(entry.getValue().concernNumberDiffStats.getValues())
              .forEach(stats.concernNumberDiffStats::addValue);
          Arrays.stream(entry.getValue().smellNumberDiffStats.getValues())
              .forEach(stats.smellNumberDiffStats::addValue);
          Arrays.stream(entry.getValue().lcomDiffStats.getValues())
              .forEach(stats.lcomDiffStats::addValue);
          Arrays.stream(entry.getValue().couplingIntensity.getValues())
              .forEach(stats.couplingIntensity::addValue);
          Arrays.stream(entry.getValue().couplingDispersion.getValues())
              .forEach(stats.couplingDispersion::addValue);
        }
      }

//      System.out.println("Project: " + projectResult.name);
//      for (var entry : projectStats.entrySet())
//      {
//        System.out.println("Strategy: " + entry.getKey());
//        System.out.println("Median Execution Time (min): " + (entry.getValue().computingTimeStats.getPercentile(50)) / 60000);
//        System.out.println("Median Smells Diff (before-after refactoring): " + entry.getValue().smellNumberDiffStats.getPercentile(50));
//        System.out.println("Median Concerns Per Type Diff (before-after refactoring): " + entry.getValue().concernNumberDiffStats.getPercentile(50));
//        System.out.println("Median LCOM Diff (before-after refactoring): " + entry.getValue().lcomDiffStats.getPercentile(50));
//        System.out.println("Median Number of Refactorings: " + entry.getValue().numberRefactoringsStats.getPercentile(50));
//        System.out.println("Median Euclidean Distance: " + entry.getValue().euclideanDistanceStats.getPercentile(50));
//      }

//      final ArrayList<StrategyStats> values = new ArrayList<>(projectStats.values());
//      for(int i = 0; i < values.size(); i ++) {
//        final StrategyStats firstCompared = values.get(i);
//        for(int j = i+1; j < values.size(); j ++) {
//          final StrategyStats secondCompared = values.get(j);
//          System.out.println(firstCompared.strategyName + " - " + secondCompared.strategyName);
//          System.out.println("Euclidean Distance:");
//          runMannWithney(firstCompared.euclideanDistanceStats.getValues(), secondCompared.euclideanDistanceStats.getValues(), System.out);
//          System.out.println("Number of Refactorings");
//          runMannWithney(firstCompared.numberRefactoringsStats.getValues(), secondCompared.numberRefactoringsStats.getValues(), System.out);
//          System.out.println("Execution Time");
//          runMannWithney(firstCompared.computingTimeStats.getValues(), secondCompared.computingTimeStats.getValues(), System.out);
//          System.out.println("Smells Number Difference");
//          runMannWithney(firstCompared.smellNumberDiffStats.getValues(), secondCompared.smellNumberDiffStats.getValues(), System.out);
//          System.out.println("Concerns Number Difference");
//          runMannWithney(firstCompared.concernNumberDiffStats.getValues(), secondCompared.concernNumberDiffStats.getValues(), System.out);
//          System.out.println("LCOM Difference");
//          runMannWithney(firstCompared.lcomDiffStats.getValues(), secondCompared.lcomDiffStats.getValues(), System.out);
//        }
//      }
    }
    createAggreatedStats(allStats);
    saveToFiles(allStats);
  }

  private void createAggreatedStats(final Map<String, Map<String, StrategyStats>> allStats)
  {
    Map<String, StrategyStats> aggregated = new HashMap<>();
    for (var projectStats : allStats.values()) {
      for (var entry : projectStats.entrySet()) {
        var stats = aggregated.get(entry.getKey());
        if (stats == null)
        {
          stats = new StrategyStats();
          stats.strategyName = entry.getKey();
          aggregated.put(entry.getKey(), stats);
        }
        Arrays.stream(entry.getValue().computingTimeStats.getValues())
            .forEach(stats.computingTimeStats::addValue);
        Arrays.stream(entry.getValue().concernNumberDiffStats.getValues())
            .forEach(stats.concernNumberDiffStats::addValue);
        Arrays.stream(entry.getValue().euclideanDistanceStats.getValues())
            .forEach(stats.euclideanDistanceStats::addValue);
        Arrays.stream(entry.getValue().numberRefactoringsStats.getValues())
            .forEach(stats.numberRefactoringsStats::addValue);
        Arrays.stream(entry.getValue().smellNumberDiffStats.getValues())
            .forEach(stats.smellNumberDiffStats::addValue);
        Arrays.stream(entry.getValue().lcomDiffStats.getValues())
            .forEach(stats.lcomDiffStats::addValue);
        Arrays.stream(entry.getValue().couplingIntensity.getValues())
            .forEach(stats.couplingIntensity::addValue);
        Arrays.stream(entry.getValue().couplingDispersion.getValues())
            .forEach(stats.couplingDispersion::addValue);
      }
    }
    allStats.put("All", aggregated);
  }

  private void saveNonDominatedSolutions(String release, String project, final Map<String, List<SolutionDTO>> nonDominatedSolutions)
  {
    System.out.println();
    System.out.println(project);
//    Map<String, DescriptiveStatistics> solutionsGenerationsMap = new HashMap<>();
    nonDominatedSolutions.entrySet()
        .stream()
        .forEach(e ->
        {
          final List<SolutionDTO> sorted = e.getValue()
              .stream()
              .sorted((s1, s2) -> Double.compare(s1.euclideanDistance, s2.euclideanDistance))
              .collect(Collectors.toList());
          InOutUtil.saveAsJsonFile(sorted, project + "_" + release + "_" + e.getKey() + "_non_dominated.json");


            System.out.println();
            System.out.println(e.getKey());
            sorted.forEach(s ->
            {
              System.out.print(String.format("%,.5f ", s.objectives.get("NumberOfConcerns")));
              System.out.print(String.format("%,.5f ", s.objectives.get("NumberOfRefactorings")));
              System.out.print(String.format("%,.5f", s.objectives.get("Density")));
              System.out.println();
            });


//          var generations = solutionsGenerationsMap.getOrDefault(e.getKey(), new DescriptiveStatistics());
//          sorted.forEach(v -> generations.addValue(v.generation));
//          solutionsGenerationsMap.put(e.getKey(), generations);

          System.out.println();
          System.out.println(e.getKey());
          Map<String, List<Double>> objectives = new HashMap<>();
          for (var solutionDTO : sorted)
          {
            solutionDTO.objectives
                .entrySet()
                .forEach(se -> {
                  final List<Double> list = objectives.getOrDefault(se.getKey(), new ArrayList<>());
                  list.add(se.getValue());
                  objectives.put(se.getKey(), list);
                });
          }
//          System.out.println("project: " + project);
//          objectives.entrySet().forEach(obj -> System.out.println(obj.getKey() + ": " + obj.getValue()));
        });

    //System.out.println(solutionsGenerationsMap.values());
  }

  private void saveToFiles(final Map<String, Map<String, StrategyStats>> allStats)
  {
    saveAnalysisTable(allStats);
    saveMannWhitneyResults(allStats);
  }

  private void saveMannWhitneyResults(final Map<String, Map<String, StrategyStats>> allStats)
  {
    try (PrintStream printStream = new PrintStream(InOutUtil.getFileInOutputDir("statistical-tests.txt"))) {
      for (var projectStats : allStats.entrySet())
      {
        printStream.println("---------------------------------");
        printStream.println(projectStats.getKey());
        final ArrayList<StrategyStats> values = new ArrayList<>(projectStats.getValue().values());
        for (int i = 0; i < values.size(); i++)
        {
          final StrategyStats firstCompared = values.get(i);
          for (int j = i + 1; j < values.size(); j++)
          {
            final StrategyStats secondCompared = values.get(j);
            printStream.println(firstCompared.strategyName + " - " + secondCompared.strategyName);
            printStream.println("Euclidean Distance:");
            runMannWithney(firstCompared.euclideanDistanceStats.getValues(), secondCompared.euclideanDistanceStats.getValues(),printStream);
            printStream.println("Number of Refactorings");
            runMannWithney(firstCompared.numberRefactoringsStats.getValues(), secondCompared.numberRefactoringsStats.getValues(), printStream);
            printStream.println("Execution Time");
            runMannWithney(firstCompared.computingTimeStats.getValues(), secondCompared.computingTimeStats.getValues(), printStream);
            printStream.println("Smells Number Difference");
            runMannWithney(firstCompared.smellNumberDiffStats.getValues(), secondCompared.smellNumberDiffStats.getValues(), printStream);
            printStream.println("Concerns Number Difference");
            runMannWithney(firstCompared.concernNumberDiffStats.getValues(), secondCompared.concernNumberDiffStats.getValues(), printStream);
            printStream.println("LCOM Difference");
            runMannWithney(firstCompared.lcomDiffStats.getValues(), secondCompared.lcomDiffStats.getValues(), printStream);
          }
        }
      }
      printStream.flush();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void saveAnalysisTable(final Map<String, Map<String, StrategyStats>> allStats)
  {
    Map<String, Integer> algoIndexMap = new HashMap<>();
    algoIndexMap.put("BASELINE_NF", 0);
    algoIndexMap.put("BASELINE", 1);
    algoIndexMap.put("MOSA", 2);
    algoIndexMap.put("NSGAII", 3);
    try (
        BufferedWriter writer = InOutUtil.getBufferedWriter("analysis-table.csv");

        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader("Project", "BASELINE_NF-ED", "BASELINE-ED", "MOSA-ED", "NSGA-II-ED",
                "BASELINE_NF-NR", "BASELINE-NR", "MOSA-NR", "NSGA-II-NR",
                "BASELINE_NF-CT", "BASELINE-CT", "MOSA-CT", "NSGA-II-CT",
                "BASELINE_NF-SD", "BASELINE-SD", "MOSA-SD", "NSGA-II-SD",
                "BASELINE_NF-FD", "BASELINE-FD", "MOSA-FD", "NSGA-II-FD",
                "BASELINE_NF-LD", "BASELINE-LD", "MOSA-LD", "NSGA-II-LD",
                "BASELINE_NF-CI", "BASELINE-CI", "MOSA-CI", "NSGA-II-CI",
                "BASELINE_NF-CD", "BASELINE-CD", "MOSA-CD", "NSGA-II-CD"));
    ) {
      for (var entry : allStats.entrySet())
      {
        String[] euclideanDistance = new String[4];
        String[] numberRefactorings = new String[4];
        String[] computingTime = new String[4];
        String[] smellDiff = new String[4];
        String[] featureDiff = new String[4];
        String[] lcomDiff = new String[4];
        String[] couplingIntensityDiff = new String[4];
        String[] couplingDispersionDiff = new String[4];
        for (var algoEntry : entry.getValue().entrySet())
        {
          final Integer index = algoIndexMap.get(algoEntry.getKey());
          euclideanDistance[index] = String.format("%.4f", algoEntry.getValue().euclideanDistanceStats.getPercentile(50)).replaceAll("\\.", ",");
          smellDiff[index] = String.format("%.4f", algoEntry.getValue().smellNumberDiffStats.getPercentile(50)).replaceAll("\\.", ",");
          featureDiff[index] = String.format("%.4f", algoEntry.getValue().concernNumberDiffStats.getPercentile(50)).replaceAll("\\.", ",");
          lcomDiff[index] = String.format("%.4f", algoEntry.getValue().lcomDiffStats.getPercentile(50)).replaceAll("\\.", ",");
          numberRefactorings[index] = String.format("%.4f", algoEntry.getValue().numberRefactoringsStats.getPercentile(50)).replaceAll("\\.", ",");
          computingTime[index] = String.format("%.4f", algoEntry.getValue().computingTimeStats.getPercentile(50) / 60000).replaceAll("\\.", ",");

          couplingIntensityDiff[index] = String.format("%.4f", algoEntry.getValue().couplingIntensity.getPercentile(50)).replaceAll("\\.", ",");
          couplingDispersionDiff[index] = String.format("%.4f", algoEntry.getValue().couplingDispersion.getPercentile(50)).replaceAll("\\.", ",");
        }

        csvPrinter.printRecord(entry.getKey(), euclideanDistance[0], euclideanDistance[1], euclideanDistance[2], euclideanDistance[3],
            numberRefactorings[0], numberRefactorings[1], numberRefactorings[2], numberRefactorings[3],
            computingTime[0], computingTime[1], computingTime[2], computingTime[3],
            smellDiff[0], smellDiff[1], smellDiff[2], smellDiff[3],
            featureDiff[0], featureDiff[1], featureDiff[2], featureDiff[3],
            lcomDiff[0], lcomDiff[1], lcomDiff[2], lcomDiff[3],
            couplingIntensityDiff[0], couplingIntensityDiff[1], couplingIntensityDiff[2], couplingIntensityDiff[3],
            couplingDispersionDiff[0], couplingDispersionDiff[1], couplingDispersionDiff[2], couplingDispersionDiff[3]);
      }
      csvPrinter.flush();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void runMannWithney(final double[] firstValues, final double[] secondValues, PrintStream printStream)
  {
    MannWhitneyUTest test = new MannWhitneyUTest();
    final double mannWhitneyU = test.mannWhitneyU(firstValues, secondValues);
    final double mannWhitneyUTest = test.mannWhitneyUTest(firstValues, secondValues);
    printStream.println("mannWhitney U: " + mannWhitneyU);
    printStream.print("p-value: " + mannWhitneyUTest);
    if (mannWhitneyUTest < 0.05) {
      printStream.println(" < 0.05");
    } else {
      printStream.println(" > 0.05");
    }
  }

}
