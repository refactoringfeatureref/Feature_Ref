package br.featureref.organicref.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import br.featureref.organicref.model.builder.TypesBuilder;

public class SourceFileASTRequestor extends FileASTRequestor {
	
	private List<TypesBuilder> sourceFiles;
	
	public SourceFileASTRequestor() {
		sourceFiles = new ArrayList<>();
	}

	@Override
	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
		TypesBuilder typesBuilder = new TypesBuilder(new File(sourceFilePath), ast);
		typesBuilder.buildTypes();
		this.sourceFiles.add(typesBuilder);
	}
	
	public List<TypesBuilder> getSourceFiles() {
		return sourceFiles;
	}
}
