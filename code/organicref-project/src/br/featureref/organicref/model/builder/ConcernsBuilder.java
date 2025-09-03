package br.featureref.organicref.model.builder;

import java.util.Map;
import java.util.Map.Entry;

import br.featureref.organicref.concerns.extraction.topicmodeling.ConcernsInferencer;
import br.featureref.organicref.concerns.extraction.topicmodeling.FeatureSequenceCreator;
import br.featureref.organicref.concerns.extraction.topicmodeling.TopicsModelCreator;
import br.featureref.organicref.model.entities.Concern;
import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.ElementConcern;
import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.exceptions.NoTopicModelException;
import cc.mallet.types.InstanceList;

public class ConcernsBuilder {
	
	private FeatureSequenceCreator featureSequenceCreator;
	private int numberOfConcerns;
	private boolean skipMethods;

	public ConcernsBuilder(int numberOfConcerns, final boolean skipMethods) {
		this.numberOfConcerns = numberOfConcerns;
		this.skipMethods = skipMethods;
		this.featureSequenceCreator = new FeatureSequenceCreator();
	}
	
	public void buildConcernsForProject(Project project) {
		project.clearAllConcerns();
		ConcernsInferencer inferencer = createInferencer(project);
		project.setConcernsInferencer(inferencer);
		buildElementConcerns(project, inferencer);
	}

	private void buildElementConcerns(Project project, ConcernsInferencer inferencer) {
		for (Type type : project.getAllTypes()) {
			Map<Concern, Double> map = inferencer.inferConcernsForElement(type.getTextualRepresentation());
			for (Entry<Concern, Double> entry : map.entrySet()) {
				type.addElementConcern(new ElementConcern(entry.getKey(), entry.getValue()));
			}
			
			if (type.getElementConcerns().size() > 0 && !skipMethods) {
				buildMethodsConcerns(inferencer, type);
			}
		}
	}

	public static void buildMethodsConcerns(ConcernsInferencer inferencer, Type type) {
		for (Element method : type.getMethods()) {
			Map<Concern, Double> map = inferencer.inferConcernsForElement(method.getTextualRepresentation());
			for (Entry<Concern, Double> entry : map.entrySet()) {
				method.addElementConcern(new ElementConcern(entry.getKey(), entry.getValue()));
			}
			
			if (method.getElementConcerns().size() == 0 && !type.getElementConcerns().isEmpty()) {
				ElementConcern typeConcern = type.getElementConcerns().first();
				method.addElementConcern(new ElementConcern(typeConcern.getConcern(), typeConcern.getProbability()));
			}
		}
	}

	private ConcernsInferencer createInferencer(Project project) {
		try {
			InstanceList instances = featureSequenceCreator.createInstancesForData(project.getTypesAsDocumentsForConcerns());
			TopicsModelCreator topicsModelCreator = new TopicsModelCreator(instances, numberOfConcerns, true);
			topicsModelCreator.buildModel();
			return topicsModelCreator.getConcernsInferencer();
		} catch (NoTopicModelException ex) {
			return null;
		}
	}
	
	

}
