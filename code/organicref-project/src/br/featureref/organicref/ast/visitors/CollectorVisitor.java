package br.featureref.organicref.ast.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;

public abstract class CollectorVisitor<T> extends ASTVisitor {
	
	private Set<T> nodesCollected;
	
	public CollectorVisitor() {
		nodesCollected = new HashSet<>();
	}
	
	protected void addCollectedNode(T node) {
		this.nodesCollected.add(node);
	}
	
	protected boolean wasAlreadyCollected(T node) {
		return this.nodesCollected.contains(node);
	}
	
	public List<T> getNodesCollected() {
		return new ArrayList<>(nodesCollected);
	}
	
	public void clearNodesCollected() {
		nodesCollected.clear();
	}
}
