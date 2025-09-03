package br.featureref.organicref.concerns.extraction.topicmodeling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;

import br.featureref.organicref.model.entities.Concern;
import br.featureref.organicref.model.exceptions.InvalidStateException;
import br.featureref.organicref.model.exceptions.NoTopicModelException;
import br.featureref.organicref.util.OrganicRefOptions;
import br.featureref.organicref.util.InOutUtil;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.topics.TopicModelDiagnostics;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

public class TopicsModelCreator {

	private final InstanceList instances;
	private int numTopics;
	private static final int NUM_THREADS = 2;
	private int numIterations;
	private ParallelTopicModel model = null;
	private TopicInferencer inferencer;
	private ConcernsInferencer concernsInferencer;
	private boolean saveModel;

	public TopicsModelCreator(InstanceList instances, int numTopics, boolean saveModel) {
		if (instances == null) {
			throw new InvalidParameterException("The InstanceList parameter should not be null.");
		}

		this.instances = instances;
		this.numTopics = numTopics;
		// TODO receive from user configuration
		this.numIterations = 1500;
		this.saveModel = saveModel;
	}

	public void buildModel() {
		try {
			tryLoadingExistingModel();
			if (inferencer == null) {
				buildNewModel();
			}
		} catch (Exception e) {
			System.err.println("Failed to build topic model");
			System.err.println(e);
			throw new NoTopicModelException("Failed to build topic model");
		} finally {
			concernsInferencer = null;
		}
	}

	private void tryLoadingExistingModel() throws Exception
	{
		String concernsModelPath = OrganicRefOptions.getInstance().getValue(OrganicRefOptions.CONCERNS_MODEL_DIR);
		if (concernsModelPath != null) {
			final File modelFile = new File(FilenameUtils.concat(concernsModelPath, "topics-model.gz"));
			final File inferencerFile = new File(FilenameUtils.concat(concernsModelPath, "inferencer.mallet"));
			if (modelFile.exists() && inferencerFile.exists()) {
				model = ParallelTopicModel.read(modelFile);

				// Use two parallel samplers, which each look at one half the corpus and combine
				// statistics after every iteration.
				model.setNumThreads(NUM_THREADS);

				// Run the model for 50 iterations and stop (this is for testing only,
				// for real applications, use 1000 to 2000 iterations)
				model.setNumIterations(numIterations);

				//TODO check how to improve
				model.setOptimizeInterval(10);
				model.setBurninPeriod(20);
				model.setSymmetricAlpha(false);

				inferencer = TopicInferencer.read(inferencerFile);
			}
		}
	}

	private void buildNewModel() throws IOException
	{
		model = new ParallelTopicModel(numTopics, 50.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and combine
		// statistics after every iteration.
		model.setNumThreads(NUM_THREADS);

		// Run the model for 50 iterations and stop (this is for testing only,
		// for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(numIterations);
		//TODO check how to improve
		model.setOptimizeInterval(50);
		model.setBurninPeriod(200);
		model.setSymmetricAlpha(false);
		model.estimate();

		inferencer = model.getInferencer();

		if (saveModel) {
			model.write(InOutUtil.getFileInOutputDir("topics-model.gz"));
			model.printTopWords(InOutUtil.getFileInOutputDir("top-words.txt"), 10, true);
			saveDiagnostic();
			try {

				ObjectOutputStream oos =
						new ObjectOutputStream(new FileOutputStream(InOutUtil.getFileInOutputDir("inferencer.mallet")));
				oos.writeObject(inferencer);
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void saveDiagnostic()
	{
		try(PrintWriter out = new PrintWriter(InOutUtil.getFileInOutputDir("diagnostic.xml")))
		{
			TopicModelDiagnostics diagnostics = new TopicModelDiagnostics(model, 10);
			out.println(diagnostics.toXML());
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public ConcernsInferencer getConcernsInferencer() {
		if (model == null || inferencer == null) {
			throw new InvalidStateException("Should build the model before getting a concern inferencer.");
		}

		if (concernsInferencer == null) {
			concernsInferencer = new ConcernsInferencer(instances, inferencer, getTopicIdsToConcernsMap());
		}

		return concernsInferencer;
	}

	private Map<Integer, Concern> getTopicIdsToConcernsMap() {
		Map<Integer, Concern> topicIdsToConcernsMap = new HashMap<>();

		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		for (int topic = 0; topic < numTopics; topic++) {
			TreeSet<IDSorter> sortedWords = topicSortedWords.get(topic);
			int word = 1;
			Iterator<IDSorter> iterator = sortedWords.iterator();

			List<String> wordsInConcern = new ArrayList<>();
			while (iterator.hasNext() && word < model.wordsPerTopic) {
				IDSorter info = iterator.next();
				wordsInConcern.add(model.alphabet.lookupObject(info.getID()).toString());
				word++;
			}

			Concern newConcern = new Concern(topic, wordsInConcern);
			topicIdsToConcernsMap.put(topic, newConcern);
		}

		return topicIdsToConcernsMap;
	}
}
