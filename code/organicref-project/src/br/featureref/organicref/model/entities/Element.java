package br.featureref.organicref.model.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * This class represents a source code element, which can be either a Type (i.e., Class or Interface),
 * Method, or Field (Attribute).  
 *  
 *
 */
public abstract class Element extends Observable {
	
	private transient ASTNode node;
	
	private String fullyQualifiedName;
	private String name;
	private String kind;
	private int originalStartLineNumber;
	private int originalEndLineNumber;
	private int originalLength;
	private String textualRepresentation = null;

	private SortedSet<ElementConcern> elementConcerns;

	private List<AnnotationOccurrence> annotationOccurrences;

	public Element(ASTNode node, String kind, int startLineNumber, int endLineNumber) {
		this(kind, startLineNumber, endLineNumber, node.getLength());
		this.node = node;
	}
	
	public Element(String kind, int startLineNumber, int endLineNumber, int length) {
		this.node = null;
		this.kind = kind;
		this.originalStartLineNumber = startLineNumber;
		this.originalEndLineNumber = endLineNumber;
		this.originalLength = length;
		//We want the highest probability first
		this.elementConcerns = new TreeSet<>(Comparator.reverseOrder());
		this.annotationOccurrences = new ArrayList<>();
	}
	
	public String getKind() {
		return kind;
	}

	public ASTNode getNode() {
		return node;
	}
	
	/**
	 * Line in the source file where node starts
	 * @return line where node starts
	 */
	public int getStartLineNumber() {
		return this.originalStartLineNumber;
	}
	
	public int getEndLineNumber() {
		return this.originalEndLineNumber;
	}
	
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public void clearTextualRepresentation()
	{
		textualRepresentation = null;
	}

	protected void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public int getOriginalLength() {
		return originalLength;
	}
	
	public String getIdentifier() {
		return this.getFullyQualifiedName();
	}
	
	public abstract List<String> getListOfBasicTokens();
	
	public String getTextualRepresentation() {
		if (textualRepresentation == null)
		{
			//TODO ignore common tokens (get, set, equals, etc)
			StringBuilder builder = new StringBuilder();
			for (String token : getListOfBasicTokens())
			{
				builder.append(" ");
				builder.append(token);
			}
			textualRepresentation = builder.toString();
		}
		
		return textualRepresentation;
	}

	public void setTextualRepresentation(final String textualRepresentation)
	{
		this.textualRepresentation = textualRepresentation;
	}

	public void addElementConcern(ElementConcern elementConcern) {
		if (elementConcern != null) {
			elementConcern.setParentElement(this);
			this.elementConcerns.add(elementConcern);
		}
	}

	public void clearElementConcerns() {
		this.elementConcerns.clear();
	}

	public SortedSet<ElementConcern> getElementConcerns() {
		return this.elementConcerns;
	}

	public void addAnnotation(AnnotationOccurrence annotationOccurrence) {
		this.annotationOccurrences.add(annotationOccurrence);
	}
	
	public List<AnnotationOccurrence> getAnnotationOccurrences() {
		return this.annotationOccurrences;
	}
}
