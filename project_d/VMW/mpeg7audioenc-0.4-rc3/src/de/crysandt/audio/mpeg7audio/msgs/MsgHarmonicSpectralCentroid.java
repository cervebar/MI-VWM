/*
  Copyright (c) 2004, Michele Bartolucci
  
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class MsgHarmonicSpectralCentroid extends Msg{
	
	public float hsc;
	
	public MsgHarmonicSpectralCentroid(int time, int duration, float hsc) {
		super(time, duration);
		this.hsc = hsc;
		
	}
	
}
