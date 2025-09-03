package br.featureref.organicref.tests.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.featureref.organicref.model.entities.Type;
import br.featureref.organicref.model.builder.TypesBuilder;
import br.featureref.organicref.util.resources.JavaFilesFinder;
import br.featureref.organicref.util.resources.SourceFilesLoader;

public class TypeLoader {

	public static Type loadOne(File file) throws IOException {
		Type type = loadAll(file).get(0);
		return type;
	}
	
	public static List<Type> loadAll(File file) throws IOException {
		JavaFilesFinder finder = new JavaFilesFinder(new File("test").getAbsolutePath());
		SourceFilesLoader loader = new SourceFilesLoader(finder, file);
		TypesBuilder source = loader.getTypeBuilders().get(0);
		return source.getTypes();
	}
	
	public static List<Type> loadAllFromDir(File sourcePath) throws IOException {
		JavaFilesFinder finder = new JavaFilesFinder(sourcePath.getAbsolutePath());
		SourceFilesLoader loader = new SourceFilesLoader(finder);
		List<Type> types = new ArrayList<>();
		for (TypesBuilder source : loader.getTypeBuilders()) {
			types.addAll(source.getTypes());
		}
		return types;
	}
	

}
