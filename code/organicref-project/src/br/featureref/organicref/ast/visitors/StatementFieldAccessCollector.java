package br.featureref.organicref.ast.visitors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Visits a method body in order to find all accesses to local class fields. During
 * SimpleName visits this visitor uses binding to determine if the simple name refers
 * to a local field or no.  
 * 
 *  
 */
public class StatementFieldAccessCollector extends CollectorVisitor<FieldDeclaration> {

	/**
	 * Fields declared in the type associated with this field.
	 * Using these declarations we can determine whether a
	 * simple name is a field access or not
	 */
	private Set<IBinding> fieldBindings;
	
	private Map<IBinding, FieldDeclaration> bindingsToDeclarations;
	
	private void resolveFieldBindings(Collection<FieldDeclaration> fieldDeclarations) {
		this.fieldBindings = new HashSet<>();
		this.bindingsToDeclarations = new HashMap<>();
		for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
			Object obj = fieldDeclaration.fragments().get(0);
			if(obj instanceof VariableDeclarationFragment){
				IBinding binding = ((VariableDeclarationFragment) obj).getName().resolveBinding();
				this.fieldBindings.add(binding);
				bindingsToDeclarations.put(binding, fieldDeclaration);
			}
		}
	}
	
	public StatementFieldAccessCollector(Collection<FieldDeclaration> fieldDeclarations) {
		this.resolveFieldBindings(fieldDeclarations);
	}

	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		/*
		 * Checks if the binding refers to a variable access. If yes,
		 * checks if the variable is a field.
		 */
		if (binding != null && binding.getKind() == IBinding.VARIABLE) {
			if (this.fieldBindings.contains(binding)) {
				FieldDeclaration declaration = this.bindingsToDeclarations.get(binding);
				this.addCollectedNode(declaration);
			}
		}
		return true;
	};
}
