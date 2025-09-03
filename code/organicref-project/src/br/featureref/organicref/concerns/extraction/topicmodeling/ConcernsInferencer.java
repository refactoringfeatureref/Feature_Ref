package br.featureref.organicref.concerns.extraction.topicmodeling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import br.featureref.organicref.model.entities.Concern;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class ConcernsInferencer
{

  private InstanceList instances;
  private TopicInferencer topicInferencer;
  private Map<Integer, Concern> topicIdsToConcernsMap;
  private Map<String, Map<Concern, Double>> textToInferenceMap;

  //numIterations The total number of iterations of sampling per document
  private int numIterations = 1500;
  //thinning      The number of iterations between saved samples
  private int thinning = 10;
  //burnIn        The number of iterations before the first saved sample
  private int burnIn = 20;
  //threshold     The minimum proportion of a given topic that will be written
  private double threshold = 0.15;
  //max           The total number of topics to report per document
  private int max = 6;

  public ConcernsInferencer(InstanceList instances, TopicInferencer inferencer, Map<Integer, Concern> topicIdsToConcernsMap)
  {
    this.instances = instances;
    this.topicInferencer = inferencer;
    this.topicIdsToConcernsMap = topicIdsToConcernsMap;
    this.textToInferenceMap = new HashMap<>();
  }

  public List<Concern> getAllConcerns()
  {
    return new ArrayList<Concern>(topicIdsToConcernsMap.values());
  }

  public synchronized Map<Concern, Double> inferConcernsForElement(String textualRepresentation)
  {
    Optional<Map<Concern, Double>> cachedInference = getCachedInference(textualRepresentation);
    if (cachedInference.isPresent())
    {
      return cachedInference.get();
    }

    InstanceList testing = new InstanceList(instances.getPipe());
    testing.addThruPipe(new Instance(textualRepresentation, null, "infered instance", null));

    Map<Concern, Double> concernsDistributionMap = new HashMap<>();

    Map<Integer, Double> inferredDistributions = getInferredDistributions(testing.get(0));
    for (Entry<Integer, Double> entry : inferredDistributions.entrySet())
    {
      Concern concern = getConcernById(entry.getKey());
      if (concern != null)
      {
        concernsDistributionMap.put(concern, entry.getValue());
      }
    }

    this.textToInferenceMap.put(textualRepresentation, concernsDistributionMap);
    return concernsDistributionMap;
  }

  private Optional<Map<Concern, Double>> getCachedInference(final String textualRepresentation)
  {
    if (this.textToInferenceMap.containsKey(textualRepresentation))
    {
      return Optional.of(this.textToInferenceMap.get(textualRepresentation));
    }
    return Optional.empty();
  }

  public Concern getConcernById(Integer id)
  {
    return topicIdsToConcernsMap.get(id);
  }

  private Map<Integer, Double> getInferredDistributions(Instance instance)
  {

    int numConcerns = topicIdsToConcernsMap.size();
    IDSorter[] sortedTopics = new IDSorter[numConcerns];
    for (int topic = 0; topic < numConcerns; topic++)
    {
      // Initialize the sorters with dummy values
      sortedTopics[topic] = new IDSorter(topic, topic);
    }

    if (max < 0 || max > numConcerns)
    {
      max = numConcerns;
    }

    double[] topicDistribution = topicInferencer.getSampledDistribution(instance, numIterations, thinning, burnIn);

    for (int topic = 0; topic < numConcerns; topic++)
    {
      sortedTopics[topic].set(topic, topicDistribution[topic]);
    }
    Arrays.sort(sortedTopics);

    Map<Integer, Double> result = new HashMap<>();
    for (int i = 0; i < max; i++)
    {
      if (sortedTopics[i].getWeight() < threshold)
      {
        break;
      }
      result.put(sortedTopics[i].getID(), sortedTopics[i].getWeight());
    }

    return result;
  }
}
