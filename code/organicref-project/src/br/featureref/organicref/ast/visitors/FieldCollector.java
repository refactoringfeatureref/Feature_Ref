package br.featureref.organicref.ast.visitors;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class FieldCollector extends CollectorVisitor<VariableDeclarationFragment> {

	@Override
	public boolean visit(FieldDeclaration node) {
		boolean collected = false;
		for (Object obj : node.fragments()) {
			if(obj instanceof VariableDeclarationFragment){
				super.addCollectedNode((VariableDeclarationFragment)obj);
				collected = true;
			}
		}
		return collected;
	}
}
