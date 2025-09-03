package br.featureref.organicref.model.builder;

import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import br.featureref.organicref.model.entities.AnnotationOccurrence;
import br.featureref.organicref.model.entities.Element;
import br.featureref.organicref.model.entities.Field;
import br.featureref.organicref.model.entities.Method;
import br.featureref.organicref.model.entities.Type;

public class AnnotationBuilder {

	public static void createAnnotationOccurrences(Type type, ITypeBinding typeBinding) {
		createAnnotationOccurrencesForElement(type, typeBinding);
	}

	public static void createAnnotationOccurrences(Field field, IBinding binding) {
		createAnnotationOccurrencesForElement(field, binding);
	}

	public static void createAnnotationOccurrences(Method method, IBinding binding) {
		createAnnotationOccurrencesForElement(method, binding);
	}

	private static void createAnnotationOccurrencesForElement(Element element, IBinding binding) {
		if (binding != null && element != null) {
			for (IAnnotationBinding annotationBinding : binding.getAnnotations()) {
				String annotationName = annotationBinding.getName();
				element.addAnnotation(new AnnotationOccurrence(annotationName));
			}
		}
	}

}
