package br.featureref.organicref.quality.symptoms.detectors;

import java.util.ArrayList;
import java.util.List;

import br.featureref.organicref.model.entities.Project;
import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.quality.symptoms.TypeConcernConcentration;

public class TypeConcernConcentrationDetector {

	public static List<TypeConcernConcentration> detect(Project project) {
		List<TypeConcernConcentration> symptoms = new ArrayList<>();
		
		float median = calcMedianNumberOfConcerns(project);
		
		for (Type type : project.getAllTypes()) {
			if (type.getElementConcerns().size() > median) {
				symptoms.add(new TypeConcernConcentration(type, type.getElementConcerns()));
			}
		}
		
		return symptoms;
	}
	
	private static float calcMedianNumberOfConcerns(Project project) {
		//TODO replace the mean by the median
		float sum = .0f;
		for (Type type : project.getAllTypes()) {
			sum += type.getElementConcerns().size();
		}
		return sum / project.getAllTypes().size();
	}
}
