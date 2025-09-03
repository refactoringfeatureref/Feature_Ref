package br.featureref.organicref.context.strategies;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.featureref.organicref.context.Context;
import br.featureref.organicref.context.ContextDetectionStrategy;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;

public class ChangedFilesContextDetectionStrategy implements ContextDetectionStrategy
{
 private final String sourcePath;

 public ChangedFilesContextDetectionStrategy(String sourcePath) {
  this.sourcePath = sourcePath;
 }

 public void createContext(Project project) {
  Context context = new Context();

  ProcessBuilder builder = new ProcessBuilder("git", "log", "--all", "--name-only", "--pretty=\"format:\"",
      "*.java");
  builder.directory(new File(sourcePath));
  builder.redirectErrorStream(true);
  Process process;
  try {
   process = builder.start();
   Set<String> changedFiles = getResults(process);
   for (Type type : project.getAllTypes()) {
    if (changedFiles.contains(type.getAbsoluteFilePath().replaceAll("\\\\", "/"))) {
     context.add(type);
    }
   }
  } catch (IOException e) {
   System.err.println("Unable to collect source code change data from git.");
   System.err.println(e.getMessage());
  }
 }

 private Set<String> getResults(Process process) throws IOException {
  Set<String> resultingLines = new HashSet<>();
  BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
  String line = "";
  while ((line = reader.readLine()) != null) {
   resultingLines.add(line);
  }
  return resultingLines;
 }
}
