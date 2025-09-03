package br.featureref.organicref.design.specification.exceptions;

public class CannotOpenSpecificationFileException extends RuntimeException
{
  public CannotOpenSpecificationFileException(String filePath) {
    super(String.format("Unnable to open specification file. Path: %s", filePath));
  }
}
