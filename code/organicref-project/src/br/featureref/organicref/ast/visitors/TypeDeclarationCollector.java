package br.featureref.organicref.ast.visitors;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeDeclarationCollector extends CollectorVisitor<AbstractTypeDeclaration> {
	
	@Override
	public boolean visit(TypeDeclaration node) {
		super.addCollectedNode(node);
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		super.addCollectedNode(node);
		return true;
	}
}
