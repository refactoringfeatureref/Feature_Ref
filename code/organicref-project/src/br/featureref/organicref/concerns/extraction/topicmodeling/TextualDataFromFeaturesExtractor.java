package br.featureref.organicref.concerns.extraction.topicmodeling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextualDataFromFeaturesExtractor
{
  private final String sourcePath;

  public TextualDataFromFeaturesExtractor(String sourcePath) {
    this.sourcePath = sourcePath;
  }

  public List<String> extractData() {
    try
    {
      final List<Path> paths = Files.walk(Paths.get(sourcePath))
          .filter(Files::isRegularFile)
          .filter(f -> f.getFileName().toString().endsWith(".feature"))
          .collect(Collectors.toList());
      return paths.stream()
          .map(path ->
          {
            try
            {
              return Files.readString(path).replaceAll("[^a-zA-Z]", " ").toLowerCase();
            }
            catch (IOException e)
            {
              return "";
            }
          })
          .collect(Collectors.toList());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }
}
