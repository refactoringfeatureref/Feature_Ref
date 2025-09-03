package br.featureref.organicref.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.featureref.organicref.optimization.results.dtos.OptimizationResultDTO;

public class InOutUtil
{
  public static <T> boolean saveAsJsonFileWithCurrentDate(T object, String filename)
  {
    return saveAsJsonFile(object, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-S_")) + filename);
  }

  public static <T> boolean saveAsJsonFile(T object, String filename)
  {
    File directory = getOutputDir();
    String filePath = FilenameUtils.concat(directory.getAbsolutePath(), filename);

    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    try
    {
      mapper.writeValue(new FileWriter(filePath), object);
      return true;
    }
    catch (Exception e)
    {
      System.out.println(e);
      return false;
    }
  }

  public static File getFileInOutputDir(String filename)
  {
    File directory = getOutputDir();
    return new File(FilenameUtils.concat(directory.getAbsolutePath(), filename));
  }

  public static File getOutputDir()
  {
    String outputFolder = OrganicRefOptions.getInstance().getValue(OrganicRefOptions.OUTPUT_FOLDER);

    File directory = new File(outputFolder);
    if (!directory.exists())
    {
      directory.mkdirs();
    }
    return directory;
  }

  public static <T> Optional<T> parseFileToDTO(File file, Class<T> clazz)
  {
    ObjectMapper mapper = new ObjectMapper();
    final T value;
    try
    {
      value = mapper.readValue(file, clazz);
      return Optional.ofNullable(value);
    }
    catch (Exception e)
    {
      System.out.println("Unable to read or parse results file: " + file.getAbsolutePath());
      return Optional.empty();
    }
  }

  public static BufferedWriter getBufferedWriter(final String fileName) throws IOException
  {
    return Files.newBufferedWriter(Paths.get(getFileInOutputDir( fileName).getAbsolutePath()));
  }
}
