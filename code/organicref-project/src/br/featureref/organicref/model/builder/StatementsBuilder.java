package br.featureref.organicref.model.builder;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
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

import br.featureref.organicref.ast.visitors.StatementsCollector;
import br.featureref.organicref.model.entities.StatementAbstraction;
import br.featureref.organicref.model.entities.StatementKind;

public class StatementsBuilder {

	private CompilationUnit compilationUnit;

	public StatementsBuilder(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public List<StatementAbstraction> buildStatements(MethodDeclaration methodDeclaration) {
		StatementsCollector statementsCollector = new StatementsCollector(methodDeclaration);
		methodDeclaration.accept(statementsCollector);

		List<Statement> nodesCollected = statementsCollector.getNodesCollected();
		return createStatementAbstractions(nodesCollected);
	}

	private List<StatementAbstraction> createStatementAbstractions(List<Statement> nodesCollected) {
		List<StatementAbstraction> statements = new ArrayList<>();

		for (Statement node : nodesCollected) {
			int startLineNumber = BuilderUtil.computeStartLineNumber(compilationUnit, node);
			int endLineNumber = BuilderUtil.computeEndLineNumber(compilationUnit, node);
			StatementKind statementKind = getStatementKind(node);

			StatementsCollector statementsCollector = new StatementsCollector(node);
			node.accept(statementsCollector);
			List<Statement> childrenNodes = statementsCollector.getNodesCollected();
			List<StatementAbstraction> children = createStatementAbstractions(childrenNodes);

			StatementAbstraction statementAbstraction = new StatementAbstraction(node, startLineNumber, endLineNumber,
					statementKind, children);
			
			statements.add(statementAbstraction);
		}

		return statements;
	}

	private StatementKind getStatementKind(Statement node) {
		if (node instanceof AssertStatement) {
			return StatementKind.ASSERT_STATEMENT;
		}

		if (node instanceof Block) {
			return StatementKind.BLOCK;
		}

		if (node instanceof BreakStatement) {
			return StatementKind.BREAK_STATEMENT;
		}

		if (node instanceof ConstructorInvocation) {
			return StatementKind.CONSTRUCTOR_INVOCATION;
		}

		if (node instanceof ContinueStatement) {
			return StatementKind.CONTINUE_STATEMENT;
		}

		if (node instanceof DoStatement) {
			return StatementKind.DO_STATEMENT;
		}

		if (node instanceof EmptyStatement) {
			return StatementKind.EMPTY_STATEMENT;
		}

		if (node instanceof EnhancedForStatement) {
			return StatementKind.ENHANCEDFOR_STATEMENT;
		}

		if (node instanceof ExpressionStatement) {
			return StatementKind.EXPRESSION_STATEMENT;
		}

		if (node instanceof ForStatement) {
			return StatementKind.FOR_STATEMENT;
		}

		if (node instanceof IfStatement) {
			return StatementKind.IF_STATEMENT;
		}

		if (node instanceof LabeledStatement) {
			return StatementKind.LABELED_STATEMENT;
		}

		if (node instanceof ReturnStatement) {
			return StatementKind.RETURN_STATEMENT;
		}

		if (node instanceof SuperConstructorInvocation) {
			return StatementKind.SUPERCONSTRUCTOR_INVOCATION;
		}

		if (node instanceof SwitchCase) {
			return StatementKind.SWITCH_CASE;
		}

		if (node instanceof SwitchStatement) {
			return StatementKind.SWITCH_STATEMENT;
		}

		if (node instanceof SynchronizedStatement) {
			return StatementKind.SYNCRONIZED_STATEMENT;
		}

		if (node instanceof ThrowStatement) {
			return StatementKind.THROW_STATEMENT;
		}

		if (node instanceof TryStatement) {
			return StatementKind.TRY_STATEMENT;
		}

		if (node instanceof TypeDeclarationStatement) {
			return StatementKind.TYPEDECLARATION_STATEMENT;
		}

		if (node instanceof VariableDeclarationStatement) {
			return StatementKind.VARIABLEDECLARATION_STATEMENT;
		}

		if (node instanceof WhileStatement) {
			return StatementKind.WHILE_STATEMENT;
		}

		if (node instanceof YieldStatement) {
			return StatementKind.YIELD_STATEMENT;
		}

		throw new InvalidParameterException("ASTNode type is not mapped!");
	}
}
