package br.featureref.organicref.util.jaccard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for calculating the Jaccard similarity and distance
 * of two given lists of Strings.
 * The algorithm is based on the implementation of Jaccard index provided by the
 * Apache Commons library.
 * 
 *
 */
public class JaccardIndexCalculator {
	
	public static Double similarity(List<String> left, List<String> right) {
		if (left  == null || right == null)
			throw new IllegalArgumentException("Parameter cannot be null");
		
		Set<String> intersectionSet = new HashSet<String>();
        Set<String> unionSet = new HashSet<String>();
        boolean unionFilled = false;
        int leftLength = left.size();
        int rightLength = right.size();
        if (leftLength == 0 || rightLength == 0) {
            return 0d;
        }

        for (int leftIndex = 0; leftIndex < leftLength; leftIndex++) {
            unionSet.add(left.get(leftIndex));
            for (int rightIndex = 0; rightIndex < rightLength; rightIndex++) {
                if (!unionFilled) {
                    unionSet.add(right.get(rightIndex));
                }
                if (left.get(leftIndex).equals(right.get(rightIndex))) {
                    intersectionSet.add(left.get(leftIndex));
                }
            }
            unionFilled = true;
        }
        return Double.valueOf(intersectionSet.size()) / Double.valueOf(unionSet.size());
	}
	
	public static Double distance(List<String> left, List<String> right) {
		if (left == null || right == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
		
        return Math.round((1 - similarity(left, right)) * 100d) / 100d;
	}
}
