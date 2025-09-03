package br.featureref.organicref.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.featureref.organicref.model.entities.Type;

public class Context
{
  private final Set<Type> typesInContext = new HashSet<>();
  private final Set<Type> typesInExpandedContext = new HashSet<>();

  public void clearContext() {
    this.typesInContext.clear();
    this.typesInExpandedContext.clear();
  }

  public boolean contains(Type type) {
    return this.typesInContext.contains(type);
  }

  public void removeType(Type type) {
    this.typesInContext.remove(type);
  }

  public Set<Type> getTypes()
  {
    return Set.copyOf(typesInContext);
  }

  public Set<Type> getTypesInExpandedContext()
  {
    return Set.copyOf(typesInExpandedContext);
  }

  public Set<Type> getAllTypes()
  {
    Set<Type> result = new HashSet<>(typesInContext);
    result.addAll(typesInExpandedContext);
    return result;
  }

  public void add(Type type) {
    this.typesInContext.add(type);
  }

  public void addToExpandedContext(Type type) {
    //we only include in the expanded context if it is not in the context already
    if (!typesInContext.contains(type)) {
      this.typesInExpandedContext.add(type);
    }
  }

  public void add(Collection<Type> types) {
    this.typesInContext.addAll(types);
  }

  public void addToExpandedContext(Collection<Type> types) {
    this.typesInExpandedContext.addAll(types);
  }

  public boolean isEmpty() {
    return this.typesInContext.isEmpty();
  }
}
