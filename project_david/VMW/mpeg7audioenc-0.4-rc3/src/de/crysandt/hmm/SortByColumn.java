/*
  Copyright (c) 2004, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.hmm;

import java.util.Comparator;

/**
  * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
  */
class SortByColumn
	implements Comparator
{
	private final int index;
	
	public SortByColumn(int index) {
		this.index = index;
	}
	
	public int compare(Object o1, Object o2) {
		return Float.compare(
				((float[]) o1)[index], 
				((float[]) o2)[index]);
	}
}