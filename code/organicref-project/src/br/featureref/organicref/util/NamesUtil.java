package br.featureref.organicref.util;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * The methods in this class were copied from the Code2Seq framework.
 *
 */
public class NamesUtil {
	
	public static final String EmptyString = "";

	public static String normalizeName(String original, String defaultString) {
        original = original.toLowerCase().replaceAll("\\\\n", "") // escaped new
                // lines
                .replaceAll("//s+", "") // whitespaces
                .replaceAll("[\"',;/\\*\\{\\}]", "") // quotes, apostrophies, commas
                .replaceAll("\\P{Print}", ""); // unicode weird characters
        String stripped = original.replaceAll("[^A-Za-z]", "");
        if (stripped.length() == 0) {
            String carefulStripped = original.replaceAll(" ", "_");
            if (carefulStripped.length() == 0) {
                return defaultString;
            } else {
                return carefulStripped;
            }
        } else {
            return stripped;
        }
    }
	
	public static ArrayList<String> splitToSubtokens(String str1) {
		if (str1 == null || str1.isBlank()) {
			return new ArrayList<>();
		}
		
        String str2 = str1.replace("|", " ");
        String str3 = str2.replaceAll("\\.", " ")
        					.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " "); 
        String str4 = str3.trim();
        return Stream.of(str4.split("(?<=[a-z])(?=[A-Z])|_|[0-9]|(?<=[A-Z])(?=[A-Z][a-z])|\\s+"))
                .filter(s -> s.length() > 0).map(s -> NamesUtil.normalizeName(s, NamesUtil.EmptyString))
                .filter(s -> s.length() > 0).collect(Collectors.toCollection(ArrayList::new));
    }
}
