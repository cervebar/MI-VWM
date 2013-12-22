/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of MPEG7AudioEnc.
*/

package de.crysandt.xml;

import java.util.*;

/**
 * @deprecated Please use DOM-Trees (org.w3c.dom.*) instead
 * 
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class XMLNode
	extends XML
{
	private static final String NEWLINE = System.getProperty("line.separator");
	protected static final boolean NEWLINE_DEFAULT = true;
	
	private final String name;
	private final Map  attr  = new TreeMap();
	private final Set  child = new TreeSet();
	
	private boolean newline = NEWLINE_DEFAULT;
	
	/**
	 * Creates a new node.
	 *
	 * @param name     Name of the node
	 * @param newline  start with a new line after begin tag
	 * @param priority Priority of node. Nodes with lower numbers appear learlier
	 *                 in leafs. This value has only to be set if the order of
	 *                 the nodes is important.
	 */
	public XMLNode(String name, boolean newline, int priority) {
		super(priority);
		this.name = name;
		this.newline = newline;
	}
	
	/**
	 * Creates a new node
	 *
	 * @param name    Name of node
	 * @param newline start with a new line after begin tag
	 */
	public XMLNode(String name, boolean newline) {
		super();
		this.name = name;
		this.newline = newline;
	}
	
	/**
	 * Creates a new node with the specified priority
	 *
	 * @param name     Name of node
	 * @param priority Priority of node
	 */
	public XMLNode(String name, int priority) {
		super(priority);
		this.name = name;
	}
	
	/**
	 * Create a new node with a new line after the
	 * start tag
	 * @param name Name of node
	 */
	public XMLNode(String name) {
		super();
		this.name = name;
	}
	
	public XMLNode setAttribute(String key, String value) {
		attr.put(key, value);
		return this;
	}
	
	public XMLNode appendChild(XML node) {
		child.add(node);
		return this;
	}
	
	/**
	 * Removes all other children of this node and sets node as new child
	 * @param node new child
	 * @return returns this node
	 */
	public XMLNode setChild(XML node) {
		child.clear();
		return appendChild(node);
	}
	
	public XMLNode getNode(XMLNode node) {
		for (Iterator i=child.iterator(); i.hasNext(); ) {
			try {
				XMLNode key = (XMLNode) i.next();
				if (key.equals(node))
					return key;
			} catch (ClassCastException e) {
				// key is not a node;
			}
		}
		
		child.add(node);
		return node;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("<" + name);
		
		for (Iterator i = attr.keySet().iterator(); i.hasNext(); ) {
			Object key   = i.next();
			Object value = attr.get(key);
			s.append(" " + key + "=\"" + value + "\"");
		}
		
		if (child.isEmpty()) {
			s.append("/>");
			s.append(NEWLINE);
		} else {
			s.append(">");
			if (newline)
				s.append(NEWLINE);
			
			for (Iterator i = child.iterator(); i.hasNext(); )
				s.append(i.next());
			
			s.append("</" + name + ">");
			s.append(NEWLINE);
		}
		
		return s.toString();
	}
	
	/**
	 * Compares this nodes to the specified object. Two nodes are equal
	 * if
	 * <ul>
	 * <li> both names are the same and
	 * <li> number of attibutes are the some and
	 * <li> every pair of (name; value) appear in both attribute sets
	 * </ul>
	 *
	 * @return returns true if the nodes are equal; false otherwise.
	 */
	public boolean equals(Object o) {
		XMLNode node = (XMLNode) o;
		
		if ((name != node.name) || (attr.size() != node.attr.size()))
			return false;
		
		for (Iterator i = attr.keySet().iterator(); i.hasNext(); ) {
			Object key = i.next();
			if (!node.attr.containsKey(key))
				return false;
			else if (!attr.get(key).equals(node.attr.get(key)))
				return false;
		}
		
		return true;
	}
	
	public int compareTo(Object o) {
		int comp = super.compareTo(o);
		
		if (comp != 0)  {
			return comp;
		} else {
			try {
				XMLNode node = (XMLNode) o;
				return name.compareTo(node.name);
			} catch (ClassCastException e) {
				return 0;
			}
		}
	}
	
	/**
	 * @return returns name of the node
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return returns map with attributes
	 */
	public Map getAttr() {
		return attr;
	}
}
