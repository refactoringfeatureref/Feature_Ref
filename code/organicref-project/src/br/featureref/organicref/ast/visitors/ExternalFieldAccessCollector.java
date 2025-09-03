package br.featureref.organicref.ast.visitors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Visits a method body in order to find all accesses to external fields. During
 * SimpleName visits this visitor uses binding to determine if the simple name refers
 * to a external field or not.  
 * 
 *  
 */
public class ExternalFieldAccessCollector extends CollectorVisitor<IVariableBinding> {

	private ITypeBinding declaringType;
	
	public ExternalFieldAccessCollector(ITypeBinding declaringType) {
		this.declaringType = declaringType;
	}

	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		/*
		 * Checks if the binding refers to a variable access. If yes,
		 * checks if the variable is an external field.
		 */
		if (binding != null && binding instanceof IVariableBinding) {
			IVariableBinding variableBinding = (IVariableBinding) binding;
			
			if (variableBinding.isField() && !declaringType.equals(variableBinding.getDeclaringClass())) {
				
				IVariableBinding declaration = variableBinding.getVariableDeclaration();
				this.addCollectedNode(declaration);
			}
		}
		return true;
	}
}
