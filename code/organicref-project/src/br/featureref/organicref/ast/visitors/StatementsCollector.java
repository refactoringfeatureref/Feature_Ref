package br.featureref.organicref.ast.visitors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.YieldStatement;

public class StatementsCollector extends CollectorVisitor<Statement> {

	private ASTNode parentNode;

	public StatementsCollector(ASTNode parentNode) {
		this.parentNode = parentNode;
	}

	@Override
	public boolean visit(AssertStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(Block node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(DoStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(SwitchCase node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(TryStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

	@Override
	public boolean visit(YieldStatement node) {
		if (node.equals(parentNode))
			return true;
		this.addCollectedNode(node);
		return false;
	}

}
