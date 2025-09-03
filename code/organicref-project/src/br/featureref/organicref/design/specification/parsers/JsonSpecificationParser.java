package br.featureref.organicref.design.specification.parsers;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.featureref.organicref.design.specification.exceptions.CannotOpenSpecificationFileException;


public abstract class JsonSpecificationParser<T>
{
  private String filePath;

  public JsonSpecificationParser(String filePath) {
    this.filePath = filePath;
  }

  public T getSpecification() {
    try
    {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(new File(filePath), getClassObject());
    }
    catch (Exception e)
    {
      throw new CannotOpenSpecificationFileException(filePath);
    }
  }

  protected abstract Class<T> getClassObject();
}
