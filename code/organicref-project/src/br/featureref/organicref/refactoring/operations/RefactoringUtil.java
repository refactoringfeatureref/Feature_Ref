package br.featureref.organicref.refactoring.operations;

public class RefactoringUtil {

	public static String toFieldName(String name) {
		StringBuilder builder = new StringBuilder();
		builder.append(Character.toLowerCase(name.charAt(0)));
		for (int i = 1; i < name.length(); i++) {
			builder.append(name.charAt(i));
		}
		
		return builder.toString();
	}

}
