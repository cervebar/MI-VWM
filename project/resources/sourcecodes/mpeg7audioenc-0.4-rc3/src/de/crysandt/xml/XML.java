/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of MPEG7AudioEnc.
*/

package de.crysandt.xml;

/**
 * @deprecated Please use DOM-Trees (org.w3c.dom.*) instead
 * 
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 * @see XMLNode, XMLCharacters
 */
public class XML
    implements Comparable
{
  private static int prio = 0;
  private final int priority;

  /**
   * Create a new XML Object with a given priority
   * @param priority Priority of node
   */
  protected XML(int priority) {
    this.priority = priority;
  }

  /**
   * Create a new XML Object. Object created later get a lower
   * priority (higher value)
   */
  protected XML() {
    this(getUniquePrio());
  }

  /**
   * Returns a unique priority. The priority decreases from
   * function call to function call
   *
   * @return returns a uniue priority.
   */
  private static synchronized int getUniquePrio() {
    return ++prio;
  }

  public int compareTo(Object o) {
    XML xml = (XML) o;
    return priority - xml.priority;
  }
}

