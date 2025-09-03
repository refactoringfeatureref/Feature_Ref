package br.featureref.organicref.model.builder;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class BuilderUtil {

	public static int computeStartLineNumber(CompilationUnit compUnit, ASTNode node) {
		return compUnit.getLineNumber(node.getStartPosition());
	}

	public static int computeEndLineNumber(CompilationUnit compUnit, ASTNode node) {
		return compUnit.getLineNumber(node.getStartPosition() + node.getLength());
	}
}
