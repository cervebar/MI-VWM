/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of MPEG7AudioEnc.
*/

package de.crysandt.xml;

/**
 * @deprecated Please use DOM-Trees (org.w3c.dom.*) instead
 * 
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class XMLCharacters
    extends XML
{
  private String c;

  public XMLCharacters(String c) {
    super();
    this.c = c;
  }

  public XMLCharacters(String c, int priority) {
    super(priority);
    this.c = c;
  }

  public String getCharacters () {
    return c;
  }

  public String toString() {
    return new String( c );
  }

  public static String String2XML( String s ) {
    String tmp = s;
    tmp = tmp.replaceAll("&", "&amp;");
    tmp = tmp.replaceAll("<", "&lt;");
    tmp = tmp.replaceAll(">", "&gt;");
    return tmp;
  }
}