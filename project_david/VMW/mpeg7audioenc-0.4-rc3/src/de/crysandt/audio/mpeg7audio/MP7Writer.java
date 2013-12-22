/*
  Copyright (c) 2002-2006, Holger Crysandt et al.
 
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;


import java.text.*;
import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.hmm.GaussianDistribution;
import de.crysandt.xml.*;

/**
 * @deprecated It's better to use MP7DocumentBuilder
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MP7Writer
implements MsgListener
{
	static final int PRIO_MEDIA_INFO    = -2;
	static final int PRIO_CREATION_INFO = -1;
	static final int PRIO_MEDIA_TIME    = 0;
	
	private static final String NEWLINE = System.getProperty("line.separator");
	private static final String SPACE = " ";
	private static final String SOS = "SeriesOfScalar";
	
	private static final float format_limit_max = 1e3f;
	private static final float format_limit_min = 1.0f / format_limit_max;
	
	private static DecimalFormat df;
	private static DecimalFormat df_exp;
	static {
		DecimalFormatSymbols locale_us = new DecimalFormatSymbols(Locale.US);
		df     = new DecimalFormat("0.########", locale_us);
		df_exp = new DecimalFormat("0.######E0", locale_us);
		df_exp.setDecimalFormatSymbols(locale_us);
	}
	private boolean checked=false;
	private int duration = -1;
	
	private MP7CreationInformation creation_info = null;
	private MP7MediaInformation media_info = null;
	private LinkedList listDC= new LinkedList();// DigitalClip
	private LinkedList listDZ= new LinkedList();// DigitalZero
	private LinkedList listSH= new LinkedList();// SampleHold
	private LinkedList listCK= new LinkedList();// Click
	private LinkedList listBNL=new LinkedList();// BackgroundNoiseLevel
	private LinkedList listDCO=new LinkedList();// DcOffset
	private LinkedList listBW= new LinkedList();// BandWidth
	private SortedSet setAW  = new TreeSet();   // AudioWaveform
	private SortedSet setASBP= new TreeSet();   // AudioSpectrumBasis/-Projection
	private SortedSet setASC = new TreeSet();   // AudioSpectrumCentroid
	private SortedSet setASD = new TreeSet();   // AudioSpectrumDistribution
	private SortedSet setASS = new TreeSet();   // AudioSpectrumSpread
	private SortedSet setASE = new TreeSet();   // AudioSpectrumEnvelope
	private SortedSet setASF = new TreeSet();   // AudioSpectrumFlatness
	private SortedSet setAS  = new TreeSet();   // AudioSignature
	private SortedSet setAP  = new TreeSet();   // AudioPower
	private SortedSet setAFF = new TreeSet();	// AudioFundamentalFrequency
	private SortedSet setAH  = new TreeSet();   // AudioHarmonicity
	private SortedSet setLAT = new TreeSet();	// LogAttackTime, although is a scalar value
	private SortedSet setHSC = new TreeSet();   // HarmonicSpectralCentroid, although is a scalar value
	private SortedSet setHSD = new TreeSet();   // HarmonicSpectralDeviation, although is a scalar value
	private SortedSet setHSS = new TreeSet();   // HarmonicSpectralSpread, although is a scalar value
	private SortedSet setHSV = new TreeSet();   // HarmonicSpectralVariation, although is a scalar value
	private SortedSet setSC  = new TreeSet(); 	// SpectralCentroid, although is a scalar value
	private SortedSet setTC  = new TreeSet();   // TemporalCentroid, although is a scalar value
	private MsgSoundModel sound_model = null;
	private LinkedList listSI= new LinkedList();  // Silence
	private LinkedList listBPM = new LinkedList();  // AudioTempoType
	
	public MP7Writer( ) {  }
	
	public void receivedMsg( Msg msg ) {
		if (msg instanceof MsgResizer)
			setDuration((MsgResizer) msg);
		else if (msg instanceof MsgDigitalClip)
		    listDC.add(msg);
		else if (msg instanceof MsgDigitalZero)
		    listDZ.add(msg);
		else if (msg instanceof MsgSampleHold)
		    listSH.add(msg);
		else if (msg instanceof MsgClick)
		    listCK.add(msg);
		else if (msg instanceof MsgDcOffset)
		    listDCO.add(msg);
		else if (msg instanceof MsgBandWidth)
			listBW.add(msg);
		else if (msg instanceof MsgAudioSignature)
			setAS.add(msg);
		else if (msg instanceof MsgAudioSpectrumBasisProjection)
			setASBP.add(msg);
		else if (msg instanceof MsgAudioSpectrumCentroid)
			setASC.add(msg);
		else if (msg instanceof MsgAudioSpectrumDistribution)
			setASD.add(msg);
		else if (msg instanceof MsgAudioSpectrumEnvelope)
			setASE.add(msg);
		else if (msg instanceof MsgAudioSpectrumFlatness)
			setASF.add(msg);
		else if (msg instanceof MsgAudioSpectrumSpread)
			setASS.add(msg);
		else if (msg instanceof MsgAudioPower)
			setAP.add(msg);
		else if (msg instanceof MsgAudioWaveform)
			setAW.add(msg);
		else if (msg instanceof MsgAudioFundamentalFrequency)
			setAFF.add(msg);
		else if (msg instanceof MsgAudioHarmonicity)
			setAH.add(msg);
		else if (msg instanceof MsgLogAttackTime)
			setLAT.add(msg);
		else if (msg instanceof MsgHarmonicSpectralCentroid)
			setHSC.add(msg);
		else if (msg instanceof MsgHarmonicSpectralDeviation)
			setHSD.add(msg);
		else if (msg instanceof MsgHarmonicSpectralSpread)
			setHSS.add(msg);
		else if (msg instanceof MsgHarmonicSpectralVariation)
			setHSV.add(msg);
		else if (msg instanceof MsgSpectralCentroid)
			setSC.add(msg);
		else if (msg instanceof MsgTemporalCentroid)
			setTC.add(msg);
		else if (msg instanceof MsgSilence)
			listSI.addLast(msg);
		else if (msg instanceof MsgAudioTempoType)
			listBPM.addLast(msg);
		else if (msg instanceof MsgSoundModel)
			sound_model = (MsgSoundModel) msg;
	}
	
	/**
	 * @returns MPEG-7 description as XMLNode
	 */
	public XMLNode getXMLNode() {
		XMLNode audio, collection;
		XMLNode mpeg7 = new XMLNode("Mpeg7")
		.setAttribute("xmlns", "urn:mpeg:mpeg7:schema:2001")
		.setAttribute("xmlns:mpeg7", "urn:mpeg:mpeg7:schema:2001")
		.setAttribute("xmlns:xml", "http://www.w3.org/XML/1998/namespace" )
		.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		String schema_location = "urn:mpeg:mpeg7:schema:2001 mpeg7ver1.xsd";
		
		if (!setASD.isEmpty()) {
			mpeg7.setAttribute("xmlns:mpeg7hc", "de:crysandt:mpeg7");
			schema_location += "de:crysandt:mpeg7" +
			"http://www.ient.rwth-aachen.de/team/crysandt/mpeg7mds/mpeg7hc.xsd";
		}
		mpeg7.setAttribute("xsi:schemaLocation", schema_location);
		
		mpeg7.appendChild( new XMLNode( "Description" )
				.setAttribute( "xsi:type", "ContentEntityType" )
				.appendChild( new XMLNode("MultimediaContent" )
						.setAttribute( "xsi:type", "MultimediaCollectionType" )
						.appendChild( collection = new XMLNode("Collection")
								.setAttribute( "xsi:type", "SegmentCollectionType")
								.appendChild( audio = new XMLNode( "Segment" )
										.setAttribute( "xsi:type", "AudioSegmentType" )))));
		
		print_sil(collection);
		
		addInformation(audio, creation_info);
		addInformation(audio, media_info);
		
		addDuration(audio);
		addDC(audio);
		addDZ(audio);
		addSH(audio);
		addCK(audio);
		addDCO(audio);
		addBW(audio);
		addAP(audio);
		addASBP(audio);
		addASC(audio);
		addASD(audio);
		addASS(audio);
		addASE(audio);
		addASF(audio);
		addAWF(audio);
		addAFF(audio);
		addAH(audio);
		addLAT(audio);
		addHSC(audio);
		addHSD(audio);
		addHSS(audio);
		addHSV(audio);
		addSC(audio);
		addTC(audio);
		addBPM(audio);
		
		addAudioSignature(audio);
		addSoundModel(mpeg7);
		
		return mpeg7;
	}
	
	
	
	
	private void addDC(XMLNode audio) {
	    if(listDC.isEmpty()){
	        return;
	    }
	    Iterator i=listDC.iterator();
	    while (i.hasNext()){
	          MsgDigitalClip msg = (MsgDigitalClip) i.next();
	          XMLNode cliplista=null;
	          int how_many_clips=msg.getClipsnumber();
	          
	          
	          if(checked==false){
	              audio.appendChild(new XMLNode("DescriptionUnit")
	  			      .setAttribute("xsi:type" , "AudioSignalQualityType")
	                  .appendChild(cliplista=new XMLNode("ErrorEventList")));
	              checked=true;
	          }
	          else cliplista=audio;
	          for(int w=0;w<how_many_clips;w++){
	              
                   StringBuffer mtu=new StringBuffer();
                   Integer channel=new Integer(msg.channel);
                   mtu.append("PT1N"+(int)msg.SAMPLE_RATE+"F");
                   StringBuffer cp=new StringBuffer();
                   cp.append(msg.getClipposition(w));
                   StringBuffer cd=new StringBuffer();
                   cd.append(msg.getCliplength(w));
                   
	  			   cliplista.appendChild(new XMLNode("ErrorEvent")
	  			            .appendChild(new XMLNode("ErrorClass")
	  			            .setAttribute("href" , "urn:mpeg:mpeg7:cs:ErrorClassCS:digitalclip")
	  			                 .appendChild(new XMLNode("Name",false)
	  			                 .appendChild(new XMLCharacters("DigitalClip"))))
	  			            .appendChild(new XMLNode("ChannelNo",false) 
	  			                  .appendChild(new XMLCharacters(channel.toString())))
	  			            .appendChild(new XMLNode("TimeStamp")
	  			               .appendChild(new XMLNode("MediaRelIncrTimePoint",false)
	  			                     .setAttribute("mediaTimeUnit",mtu.toString())
                                     .appendChild(new XMLCharacters(cp.toString())) 
	  			                     .setAttribute("mediaTimeBase","../../MediaLocator[1]")
                                     .appendChild(new XMLCharacters("")))
                               .appendChild(new XMLNode("MediaIncrDuration",false)
                                     .setAttribute("mediaTimeUnit",mtu.toString())
                                     .appendChild(new XMLCharacters(cd.toString()))))
	  			            .appendChild(new XMLNode("Relevance",false)
	  			            .appendChild(new XMLCharacters("0")))
	  			            .appendChild(new XMLNode("DetectionProcess",false)
	  			            .appendChild(new XMLCharacters("automatic")))
	  			            .appendChild(new XMLNode("Status",false)
	  			            .appendChild(new XMLCharacters("checked")))
	  			            .appendChild(new XMLNode("Comment")
	  			              .appendChild(new XMLNode("FreeTextAnnotation",false)
	  			              .appendChild(new XMLCharacters("any comment"))  )));   
	          }
	     }
	}
	
	private void addDZ(XMLNode audio) {
	    if(listDZ.isEmpty()){
	        return;
	    }
	    Iterator i=listDZ.iterator();
	    while (i.hasNext()){
	          MsgDigitalZero msg = (MsgDigitalZero) i.next();
	          XMLNode zerolista=null;
	          int how_many_zeros=msg.getZerosnumber();
	          
	          if(checked==false){
	               audio.appendChild(new XMLNode("DescriptionUnit")
	  			        .setAttribute("xsi:type" , "AudioSignalQualityType")
	                    .appendChild(zerolista=new XMLNode("ErrorEventList")));
	               checked=true;
	          }
	          else zerolista=audio;
	          for(int w=0;w<how_many_zeros;w++){
	              
                   StringBuffer mtu=new StringBuffer();
                   Integer channel=new Integer(msg.channel);
                   mtu.append("PT1N"+(int)msg.SAMPLE_RATE+"F");
                   StringBuffer zp=new StringBuffer();
                   zp.append(msg.getZeroposition(w));
                   StringBuffer zd=new StringBuffer();
                   zd.append(msg.getZerolength(w));
                   
	  			   zerolista.appendChild(new XMLNode("ErrorEvent")
	  			            .appendChild(new XMLNode("ErrorClass")
	  			            .setAttribute("href" , "urn:mpeg:mpeg7:cs:ErrorClassCS:digitalzero")
	  			                 .appendChild(new XMLNode("Name",false)
	  			                 .appendChild(new XMLCharacters("DigitalZero"))))
	  			            .appendChild(new XMLNode("ChannelNo",false) 
	  			                  .appendChild(new XMLCharacters(channel.toString())))
	  			            .appendChild(new XMLNode("TimeStamp")
	  			               .appendChild(new XMLNode("MediaRelIncrTimePoint",false)
	  			                     .setAttribute("mediaTimeUnit",mtu.toString())
                                     .appendChild(new XMLCharacters(zp.toString())) 
	  			                     .setAttribute("mediaTimeBase","../../MediaLocator[1]")
                                     .appendChild(new XMLCharacters("")))
                               .appendChild(new XMLNode("MediaIncrDuration",false)
                                     .setAttribute("mediaTimeUnit",mtu.toString())
                                     .appendChild(new XMLCharacters(zd.toString()))))
	  			            .appendChild(new XMLNode("Relevance",false)
	  			            .appendChild(new XMLCharacters("0")))
	  			            .appendChild(new XMLNode("DetectionProcess",false)
	  			            .appendChild(new XMLCharacters("automatic")))
	  			            .appendChild(new XMLNode("Status",false)
	  			            .appendChild(new XMLCharacters("checked")))
	  			            .appendChild(new XMLNode("Comment")
	  			              .appendChild(new XMLNode("FreeTextAnnotation",false)
	  			              .appendChild(new XMLCharacters("any comment"))  )));   
	          }
	     }
	}
	
	
	
	private void addSH(XMLNode audio) {
	    if(listSH.isEmpty()){
	        return;
	    }
	    Iterator i=listSH.iterator();
	    while (i.hasNext()){
	          MsgSampleHold msg = (MsgSampleHold) i.next();
	          XMLNode shlista=null;
	          int how_many_s_holds=msg.getShnumber();
	          
	          if(checked==false){
	               audio.appendChild(new XMLNode("DescriptionUnit")
	  			        .setAttribute("xsi:type" , "AudioSignalQualityType")
	                    .appendChild(shlista=new XMLNode("ErrorEventList")));
	               checked=true;
	          }
	          else shlista=audio;
	         
	          for(int h=0;h<how_many_s_holds;h++){
	              
                   StringBuffer mtu=new StringBuffer();
                   Integer channel=new Integer(msg.channel);
                   mtu.append("PT1N"+(int)msg.SAMPLE_RATE+"F");
                   StringBuffer shp=new StringBuffer();
                   shp.append(msg.getshposition(h));
                   StringBuffer shd=new StringBuffer();
                   shd.append(msg.getshlength(h));
                   
	  			   shlista.appendChild(new XMLNode("ErrorEvent")
	  			           .appendChild(new XMLNode("ErrorClass")
	  			           .setAttribute("href" , "urn:mpeg:mpeg7:cs:ErrorClassCS:samplehold")
	  			                 .appendChild(new XMLNode("Name",false)
	  			                 .appendChild(new XMLCharacters("SampleHold"))))
	  			            .appendChild(new XMLNode("ChannelNo",false) 
	  			                  .appendChild(new XMLCharacters(channel.toString())))
	  			            .appendChild(new XMLNode("TimeStamp")
	  			               .appendChild(new XMLNode("MediaRelIncrTimePoint",false)
	  			                     .setAttribute("mediaTimeUnit",mtu.toString())
                                     .appendChild(new XMLCharacters(shp.toString())) 
	  			                     .setAttribute("mediaTimeBase","../../MediaLocator[1]")
                                     .appendChild(new XMLCharacters("")))
                               .appendChild(new XMLNode("MediaIncrDuration",false)
                                     .setAttribute("mediaTimeUnit",mtu.toString())
                                     .appendChild(new XMLCharacters(shd.toString()))))
	  			            .appendChild(new XMLNode("Relevance",false)
	  			            .appendChild(new XMLCharacters("0")))
	  			            .appendChild(new XMLNode("DetectionProcess",false)
	  			            .appendChild(new XMLCharacters("automatic")))
	  			            .appendChild(new XMLNode("Status",false)
	  			            .appendChild(new XMLCharacters("checked")))
	  			            .appendChild(new XMLNode("Comment")
	  			              .appendChild(new XMLNode("FreeTextAnnotation",false)
	  			              .appendChild(new XMLCharacters("any comment"))  )));   
	          }
	     }
	}
	
	private void addCK(XMLNode audio) {
	    if(listCK.isEmpty()){
	        return;
	    }
	    Iterator i=listCK.iterator();
	    while (i.hasNext()){
	          MsgClick msg = (MsgClick) i.next();
	          XMLNode clicklista=null;
	          int how_many_clicks=msg.getClicksnumber();
	          
	          
	          if(checked==false){
	              audio.appendChild(new XMLNode("DescriptionUnit")
	  			      .setAttribute("xsi:type" , "AudioSignalQualityType")
	                  .appendChild(clicklista=new XMLNode("ErrorEventList")));
	              checked=true;
	          }
	          else clicklista=audio;
	          for(int w=0;w<how_many_clicks;w++){
	              
                   StringBuffer mtu=new StringBuffer();
                   Integer channel=new Integer(msg.channel);
                   mtu.append("PT1N"+(int)msg.SAMPLE_RATE+"F");
                   StringBuffer cp=new StringBuffer();
                   cp.append(msg.getClickposition(w));
                   
                   
	  			   clicklista.appendChild(new XMLNode("ErrorEvent")
	  			            .appendChild(new XMLNode("ErrorClass")
	  			            .setAttribute("href" , "urn:mpeg:mpeg7:cs:ErrorClassCS:click")
	  			                 .appendChild(new XMLNode("Name",false)
	  			                 .appendChild(new XMLCharacters("Click"))))
	  			            .appendChild(new XMLNode("ChannelNo",false) 
	  			                  .appendChild(new XMLCharacters(channel.toString())))
	  			            .appendChild(new XMLNode("TimeStamp")
	  			               .appendChild(new XMLNode("MediaRelIncrTimePoint",false)
	  			                     .setAttribute("mediaTimeUnit",mtu.toString())
                                     .appendChild(new XMLCharacters(cp.toString())) 
	  			                     .setAttribute("mediaTimeBase","../../MediaLocator[1]")
                                     .appendChild(new XMLCharacters(""))))
	  			            .appendChild(new XMLNode("Relevance",false)
	  			            .appendChild(new XMLCharacters("0")))
	  			            .appendChild(new XMLNode("DetectionProcess",false)
	  			            .appendChild(new XMLCharacters("automatic")))
	  			            .appendChild(new XMLNode("Status",false)
	  			            .appendChild(new XMLCharacters("checked")))
	  			            .appendChild(new XMLNode("Comment")
	  			              .appendChild(new XMLNode("FreeTextAnnotation",false)
	  			              .appendChild(new XMLCharacters("any comment"))  )));   
	          }
	     }
	}
	
	
	
	private void addBW(XMLNode audio){
	    if(listBW.isEmpty()){
	        return;
	    }
	    XMLNode bw_lista=audio;
	    StringBuffer bw=new StringBuffer("");
	    Iterator i=listBW.iterator();
	    
	    while (i.hasNext()){
	          MsgBandWidth msg = (MsgBandWidth) i.next();
	          Integer channel=new Integer(msg.channel);
	          bw.append(msg.bw);
	          if(checked==false){
	              audio.appendChild(new XMLNode("DescriptionUnit")
	  			       .setAttribute("xsi:type" , "AudioSignalQualityType"));
	              checked=true;
	          }
	          else bw_lista=audio;
	          
	          bw_lista.appendChild(new XMLNode("BandWidth",true)
	                   .setAttribute("channels",channel.toString())
	                   .appendChild(new XMLNode("Vector",false)
	                   .appendChild(new XMLCharacters(bw.toString()))));
	    }
	}
	
	private void addDCO(XMLNode audio){
	    if(listDCO.isEmpty()){
	        return;
	    }
	    XMLNode dco_lista=audio;
	    StringBuffer dc=new StringBuffer("");
	    Iterator i=listDCO.iterator();
	    
	    while (i.hasNext()){
	          MsgDcOffset msg = (MsgDcOffset) i.next();
	          Integer channel=new Integer(msg.channel);
	          dc.append(msg.dco);
	          if(checked==false){
	              audio.appendChild(new XMLNode("DescriptionUnit")
	  			       .setAttribute("xsi:type" , "AudioSignalQualityType"));
	              checked=true;
	          }
	          else dco_lista=audio;
	          
	          dco_lista.appendChild(new XMLNode("DcOffset",true)
	                   .setAttribute("channels",channel.toString())
	                   .appendChild(new XMLNode("Vector",false)
	                   .appendChild(new XMLCharacters(dc.toString()))));
	    }
	}
	
	
	private void addBNL(XMLNode audio){
	    if(listBNL.isEmpty()){
	        return;
	    }
	    XMLNode bnl_lista=audio;
	    StringBuffer bnl=new StringBuffer();
	    Iterator i=listBNL.iterator();
	    
	    while (i.hasNext()){
	          MsgBackgroundNoiseLevel msg = (MsgBackgroundNoiseLevel) i.next();
	          Integer channel=new Integer(msg.channel);
	          bnl.append(msg.bnl);
	          if(checked==false){
	              audio.appendChild(new XMLNode("DescriptionUnit")
	  			       .setAttribute("xsi:type" , "AudioSignalQualityType"));
	              checked=true;
	          }
	          else bnl_lista=audio;
	          
	          bnl_lista.appendChild(new XMLNode("BackgroundNoiseLevel",true)
	                   .setAttribute("channels",channel.toString())
	                   .appendChild(new XMLNode("Vector",false)
	                   .appendChild(new XMLCharacters(bnl.toString()))));
	    }
	    
	}
	
	/**
	 * @param audio
	 */
	 private void addBPM(XMLNode audio) {

	  	if( listBPM.isEmpty() ) {
	  		return;
	  	}

	  	MsgAudioTempoType msg = (MsgAudioTempoType) listBPM.get(0);

	  	XMLNode raw;
	  	XMLNode weight;

	  	audio.appendChild(new XMLNode("AudioDescriptor")
	  			.setAttribute("xsi:type" , "AudioBPMType")
	  			.setAttribute("loLimit", "" + msg.loLimit)
	  			.setAttribute("hiLimit", "" + msg.hiLimit)
	  			.appendChild(new XMLNode (SOS)
	  					.setAttribute("totalNumOfSamples", "" + listBPM.size())
	  					.setAttribute("hopSize", getHopSize( msg ))
	  					.appendChild(raw = new XMLNode( "Raw" ))
	  					.appendChild(weight = new XMLNode( "Weight" )
	  			)
	  	));


	  	StringBuffer bpm = new StringBuffer( "" );
	      StringBuffer confid = new StringBuffer( "" );
	      Iterator i = listBPM.iterator();
	      while( i.hasNext() ) {
	        msg = (MsgAudioTempoType) i.next();
	        bpm.append(format(msg.bpm)).append(SPACE);
	        confid.append(format(msg.confidence)).append(SPACE);
	      }
	      raw.appendChild( new XMLCharacters(bpm.toString()) );
	      weight.appendChild( new XMLCharacters(confid.toString()) );

	  }

	private void print_sil(XMLNode collection) {
		
		if (listSI.isEmpty())
			return;
		
		Iterator i = listSI.iterator();
		while (i.hasNext()) {
			
			MsgSilence res = (MsgSilence) i.next();
			
			StringBuffer start_sil = new StringBuffer();
			int s_min_dec = res.time / 1000 / 60 / 10;
			int s_min = res.time / 1000 / 60 % 10;
			int s_sec_dec = res.time / 1000 % 60 / 10;
			int s_sec = res.time / 1000 % 60 % 10;
			int s_msec_dec = (res.time+5) % 1000 / 100;
			int s_msec = (res.time+5) % 100 /10;
			start_sil.append("T"+s_min_dec+s_min+":"+s_sec_dec+s_sec+":"+s_msec_dec+s_msec);
			
			StringBuffer dur_sil = new StringBuffer();
			dur_sil.append("PT");
			int min = res.duration / 1000 / 60;
			int sec = res.duration / 1000 % 60;
			int msec = res.duration % 1000 / 10;
			
			if (min > 0)
				dur_sil.append(min).append("M");
			
			if ((sec > 0) || (min > 0))
				dur_sil.append(sec).append("S");
			
			if ((msec > 0))
				dur_sil.append(msec).append("N100F");
			
			StringBuffer min_dur = new StringBuffer();
			int md = res.min_dur / 100;
			min_dur.append("PT" + md + "N10F");
			
			StringBuffer conf = new StringBuffer();
			conf.append(((int) ((res.conf+ 0.005) * 100)) / 100.0);
			
			collection.appendChild(new XMLNode("Segment")
					.setAttribute("xsi:type", "AudioSegmentType")
					.appendChild(new XMLNode("Header")
							.setAttribute("xsi:type", "SilenceHeaderType")
							.setAttribute("minDuration", min_dur.toString()))
							.appendChild(new XMLNode("MediaTime")
									.appendChild(new XMLNode("MediaTimePoint", false)
											.appendChild(new XMLCharacters(start_sil.toString())))
											.appendChild(new XMLNode("MediaDuration", false)
													.appendChild(new XMLCharacters(dur_sil.toString()))))
													.appendChild(new XMLNode("AudioDescriptor")
															.setAttribute("xsi:type", "SilenceType")
															.setAttribute("confidence", conf.toString())));
		}
	}
	
	/**
	 * @return returns the MPEG-7 description as String.
	 */
	public String toString() {
		return getXMLNode().toString();
	}
	
	private void setDuration( MsgResizer msg ) {
		duration = msg.time + msg.duration;
	}
	
	private static String format( double x ) {
		if (Math.abs(x) > format_limit_max)
			return df_exp.format( x );
		else if (Math.abs(x) < format_limit_min)
			if( x == 0 )
				return "0";
			else
				return df_exp.format( x );
		else
			return df.format( x );
	}
	
	private void addInformation(XMLNode audio, XMLNode info) {
		if (info != null)
			audio.appendChild(info);
	}
	
	public void setCreationInformation(MP7CreationInformation creation_info) {
		this.creation_info = creation_info;
	}
	
	public void setMediaInformation(MP7MediaInformation media_info) {
		this.media_info = media_info;
	}
	
	private void addDuration(XMLNode audio) {
		if (duration <= 0)
			return;
		
		StringBuffer dur = new StringBuffer();
		dur.append("PT");
		int min  = duration / 1000 / 60;
		int sec  = duration / 1000 % 60;
		int msec = duration % 1000;
		
		if (min > 0)
			dur.append(min).append("M");
		
		if ((sec > 0) || (min > 0))
			dur.append(sec).append("S");
		
		if ((msec > 0))
			dur.append(msec).append("N1000F");
		
		audio.appendChild(new XMLNode("MediaTime", PRIO_MEDIA_TIME)
				.appendChild(new XMLNode("MediaTimePoint", false, 0)
						.appendChild(new XMLCharacters( "T00:00:00" ) ) )
						.appendChild(new XMLNode( "MediaDuration",false, 1)
								.appendChild(new XMLCharacters(dur.toString()))));
	}
	
	private void addASBP(XMLNode audio) {
		if (setASBP.isEmpty())
			return;
		
		XMLNode xml_basis;
		XMLNode xml_projection;
		
		MsgAudioSpectrumBasisProjection msg;
		msg = (MsgAudioSpectrumBasisProjection) setASBP.first();
		
		audio.appendChild(new XMLNode("AudioDescriptor")
				.setAttribute("xsi:type", "AudioSpectrumBasisType")
				.setAttribute("loEdge", "" + msg.lo_edge)
				.setAttribute("hiEdge", "" + msg.hi_edge)
				.setAttribute("octaveResolution", getOctaveResolution(msg.resolution))
				.appendChild(new SeriesOfVector(getHopSize(msg),
						setASBP.size(),
						msg.AudioBasis[0].length)
						.appendChild(xml_basis = new XMLNode("Raw")
								.setAttribute("mpeg7:dim",
										setASBP.size() + " " +
										msg.AudioBasis.length + " " +
										msg.AudioBasis[0].length))));
		
		// don't add loEdge, hiEdge and octaveResolution here! (holger)
		audio.appendChild(new XMLNode("AudioDescriptor")
				.setAttribute("xsi:type", "AudioSpectrumProjectionType")
				.appendChild(new SeriesOfVector(getHopSize(msg),
						setASBP.size(),
						msg.AudioProjection[0].length)
						.appendChild(xml_projection = new XMLNode("Raw")
								.setAttribute("mpeg7:dim",
										"" + setASBP.size() + " " +
										msg.AudioProjection.length + " " +
										(msg.AudioProjection[0].length + 1)))));
		
		StringBuffer s_basis = new StringBuffer();
		StringBuffer s_projection = new StringBuffer();
		for (Iterator i = setASBP.iterator(); i.hasNext(); ) {
			msg = (MsgAudioSpectrumBasisProjection) i.next();
			
			float[][] basis = msg.AudioBasis;
			for (int m = 0; m < basis.length; ++m) {
				for (int n = 0; n < basis[0].length; ++n)
					s_basis.append("" + format(basis[m][n])).append(SPACE);
				s_basis.append(NEWLINE);
			}
			s_basis.append("<!-- New Block -->");
			s_basis.append(NEWLINE);
			
			float[][] projection = msg.AudioProjection;
			
			for (int m = 0; m < projection.length; ++m) {
				for (int n = 0; n < projection[0].length; ++n)
					s_projection.append("" + format(projection[m][n])).append(SPACE);
				s_projection.append(NEWLINE);
			}
			
			s_projection.append("<!-- New Block -->");
			s_projection.append(NEWLINE);
			
		} // End while(i.hasNext)
		
		xml_basis.appendChild(new XMLCharacters(s_basis.toString()));
		xml_projection.appendChild(new XMLCharacters(s_projection.toString()));
	}
	
	private void addASC(XMLNode audio) {
		// do nothing if empty
		if( setASC.isEmpty() )
			return;
		
		XMLNode raw;
		
		MsgAudioSpectrumCentroid msg;
		msg = (MsgAudioSpectrumCentroid) setASC.first();
		
		audio.appendChild(new XMLNode("AudioDescriptor")
				.setAttribute("xsi:type" , "AudioSpectrumCentroidType")
				.appendChild(new XMLNode(SOS)
						.setAttribute("hopSize", getHopSize(msg))
						.setAttribute("totalNumOfSamples", "" + setASC.size())
						.appendChild(raw = new XMLNode("Raw"))
				)
		);
		
		StringBuffer s = new StringBuffer( "" );
		Iterator i = setASC.iterator();
		while( i.hasNext() ) {
			msg = (MsgAudioSpectrumCentroid) i.next();
			s.append(format(msg.centroid)).append(SPACE);
		}
		raw.appendChild( new XMLCharacters( s.toString() ) );
	}
	
	private void addASD(XMLNode audio) {
		if (setASD.isEmpty())
			return;
		
		XMLNode distribution;
		
		MsgAudioSpectrumDistribution msg;
		msg = (MsgAudioSpectrumDistribution) setASD.first();
		
		audio.appendChild( new XMLNode("AudioDescriptor")
				.setAttribute("xsi:type", "mpeg7hc:AudioSpectrumDistributionType")
				.setAttribute("loEdge", "" + msg.lo_edge)
				.setAttribute("hiEdge", "" + msg.hi_edge)
				.setAttribute("octaveResolution", getOctaveResolution(msg.resolution))
				.appendChild(new SeriesOfVector(getHopSize(msg) ,
						setASD.size(),
						msg.getDistributionLength() )
						.appendChild(distribution = new XMLNode("Raw")
								.setAttribute("mpeg7:dim",
										setASD.size() + " " + msg.getDistributionLength()))));
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator i = setASD.iterator(); i.hasNext(); ) {
			float[] env = ( (MsgAudioSpectrumDistribution) i.next()).getDistribution();
			for (int n = 0; n < env.length; ++n)
				buffer.append(format(env[n])).append(SPACE);
			buffer.append(NEWLINE);
		}
		
		distribution.appendChild(new XMLCharacters(buffer.toString()));
	}
	
	private void addASS(XMLNode audio) {
		// do nothing if empty
		if( setASS.isEmpty() )
			return;
		
		XMLNode raw;
		
		MsgAudioSpectrumSpread msg;
		msg = (MsgAudioSpectrumSpread) setASS.first();
		
		audio.appendChild(new XMLNode("AudioDescriptor")
				.setAttribute( "xsi:type" , "AudioSpectrumSpreadType" )
				.appendChild( new XMLNode( SOS )
						.setAttribute( "hopSize", getHopSize( msg ) )
						.setAttribute( "totalNumOfSamples", "" + setASS.size() )
						.appendChild( raw = new XMLNode( "Raw" )
						)
				)
		);
		
		StringBuffer s = new StringBuffer( "" );
		Iterator i = setASS.iterator();
		while( i.hasNext() ) {
			msg = (MsgAudioSpectrumSpread) i.next();
			s.append(format(msg.spread)).append(SPACE);
		}
		
		raw.appendChild( new XMLCharacters( s.toString() ) );
	}
	
	private void addASE(XMLNode audio) {
		if( setASE.isEmpty() )
			return;
		
		XMLNode envelope;
		
		MsgAudioSpectrumEnvelope msg;
		msg = (MsgAudioSpectrumEnvelope) setASE.first();
		
		audio.appendChild( new XMLNode("AudioDescriptor")
				.setAttribute("xsi:type", "AudioSpectrumEnvelopeType")
				.setAttribute("loEdge", "" + msg.lo_edge)
				.setAttribute("hiEdge", "" + msg.hi_edge)
				.setAttribute("octaveResolution", getOctaveResolution(msg.resolution))
				.appendChild( new SeriesOfVector( getHopSize(msg) ,
						setASE.size(),
						msg.getEnvelopeLength() )
						.appendChild(envelope = new XMLNode("Raw")
								.setAttribute("mpeg7:dim",
										"" + setASE.size() + " " + msg.getEnvelopeLength())
						)));
		
		StringBuffer s = new StringBuffer();
		for(Iterator i = setASE.iterator(); i.hasNext(); ) {
			float[] env = ((MsgAudioSpectrumEnvelope) i.next()).getEnvelope();
			for( int n=0; n<env.length; ++n )
				s.append(format(env[n])).append(SPACE);
			s.append(NEWLINE);
		}
		
		envelope.appendChild( new XMLCharacters(s.toString() ) );
	}
	
	private void addASF(XMLNode audio) {
		if (setASF.isEmpty())
			return;
		
		XMLNode raw;
		
		MsgAudioSpectrumFlatness msg;
		msg = (MsgAudioSpectrumFlatness) setASF.first();
		
		audio.appendChild(new XMLNode("AudioDescriptor")
				.setAttribute("xsi:type" , "AudioSpectrumFlatnessType")
				.setAttribute("loEdge", "" + msg.lo_edge)
				.setAttribute("hiEdge", "" + msg.hi_edge)
				.appendChild(new SeriesOfVector(getHopSize(msg),
						setASF.size(),
						msg.getFlatnessLength() )
						.appendChild(raw = new XMLNode("Raw")
								.setAttribute("mpeg7:dim",
										"" + setASF.size() + " " + msg.getFlatnessLength() )
						)
				)
		);
		
		StringBuffer s = new StringBuffer();
		Iterator i = setASF.iterator();
		while( i.hasNext() ){
			float[] fl = ((MsgAudioSpectrumFlatness)i.next()).getFlatness();
			for( int n=0; n<fl.length; ++n )
				s.append("" + format(fl[n])).append(SPACE);
			s.append( NEWLINE );
		}
		raw.appendChild( new XMLCharacters( s.toString() ) );
	}
	
	
	private void addAWF(XMLNode audio) {
		if( setAW.isEmpty() )
			return;
		
		MsgAudioWaveform msg = (MsgAudioWaveform) setAW.first();
		
		XMLNode min;
		XMLNode max;
		
		audio.appendChild(new XMLNode("AudioDescriptor")
				.setAttribute( "xsi:type" , "AudioWaveformType" )
				.appendChild( new XMLNode ( SOS )
						.setAttribute( "totalNumOfSamples", "" + setAW.size() )
						.setAttribute( "hopSize", getHopSize( msg ) )
						.appendChild(min = new XMLNode("Min", false, -1))
						.appendChild(max = new XMLNode("Max", false, 0))
				)
		);
		
		StringBuffer smin = new StringBuffer( "" );
		StringBuffer smax = new StringBuffer( "" );
		Iterator i = setAW.iterator();
		while( i.hasNext() ) {
			msg = (MsgAudioWaveform) i.next();
			smin.append(format(msg.min)).append(SPACE);
			smax.append(format(msg.max)).append(SPACE);
		}
		min.appendChild( new XMLCharacters(smin.toString()) );
		max.appendChild( new XMLCharacters(smax.toString()) );
	}
	
	private void addAP(XMLNode audio) {
		if( setAP.isEmpty() )
			return;
		
		MsgAudioPower msg = (MsgAudioPower) setAP.first();
		
		XMLNode power;
		
		audio.appendChild(new XMLNode("AudioDescriptor")
				.setAttribute("xsi:type" , "AudioPowerType")
				.appendChild(new XMLNode (SOS)
						.setAttribute("totalNumOfSamples", "" + setAP.size())
						.setAttribute("hopSize", getHopSize( msg ))
						.appendChild(power = new XMLNode( "Raw", false ))
				)
		);
		
		StringBuffer s = new StringBuffer( "" );
		Iterator i = setAP.iterator();
		while( i.hasNext() ) {
			msg = (MsgAudioPower) i.next();
			assert msg.db_scale==false;
			s.append(format(msg.power)).append(SPACE);
		}
		power.appendChild( new XMLCharacters(s.toString()) );
	}
	
	private void addAudioSignature( XMLNode audio ) {
		if( setAS.isEmpty() )
			return;
		
		MsgAudioSignature msg = (MsgAudioSignature) setAS.first();
		
		XMLNode mean;
		XMLNode var;
		
		audio.appendChild( new XMLNode( "AudioDescriptionScheme" )
				.setAttribute( "xsi:type", "AudioSignatureType" )
				.appendChild( new XMLNode( "Flatness" )
						.setAttribute( "loEdge", "" + msg.lo_edge )
						.setAttribute( "hiEdge", "" + msg.hi_edge )
						.appendChild( new SeriesOfVector( getHopSize(msg),
								msg.decimation * setAS.size(),
								msg.getLength() )
								.appendChild( new XMLNode( "Scaling" )
										.setAttribute( "numOfElements", "" + setAS.size() )
										.setAttribute( "ratio", "" + msg.decimation )
								)
								.appendChild( mean = new XMLNode( "Mean" )
										.setAttribute("mpeg7:dim",
												"" + setAS.size() + " " + msg.getLength()))
												.appendChild( var = new XMLNode( "Variance" )
														.setAttribute("mpeg7:dim", ""+setAS.size()+" "+msg.getLength()))
						)
				)
		);
		
		StringBuffer smean = new StringBuffer();
		StringBuffer svar = new StringBuffer();
		
		Iterator i = setAS.iterator();
		while (i.hasNext()) {
			msg = (MsgAudioSignature) i.next();
			float[] m = msg.getFlatnessMean();
			float[] v = msg.getFlatnessVariance();
			for( int n=0; n<msg.getLength(); ++n ) {
				smean.append(format(m[n])).append(SPACE);
				svar.append(format(v[n])).append(SPACE);
			}
			smean.append(NEWLINE);
			svar.append(NEWLINE);
		}
		
		mean.appendChild( new XMLCharacters( smean.toString()));
		var.appendChild( new XMLCharacters( svar.toString()));
	}
	
	private void addSoundModel(XMLNode mpeg7) {
		if (sound_model == null)
			return;
		
		XMLNode model;
		
		mpeg7.appendChild(new XMLNode("Description")
				.setAttribute( "xsi:type", "ModelDescriptionType")
				.appendChild(new XMLNode("Model")
						.setAttribute( "xsi:type", "SoundClassificationModelType" )
						.appendChild( model = new XMLNode("SoundModel")
								.setAttribute("xsi:type", "SoundModelType")
								.setAttribute("numOfStates", "" + sound_model.hmm.N))));
		
		// set init
		float[] init = sound_model.hmm.getInit();
		StringBuffer s = new StringBuffer("" + init[0]);
		for (int i=1; i<init.length; ++i)
			s.append(SPACE).append(format(init[i]));
		model.appendChild(new XMLNode("Initial", false)
				.setAttribute( "mpeg7:dim", "1 " + init.length)
				.appendChild(new XMLCharacters(s.toString())));
		
		// set transitions
		float[][] trans = sound_model.hmm.getTransitions();
		s = new StringBuffer();
		for (int i=0; i<trans.length; ++i) {
			float[] row = trans[i];
			for (int j = 0; j < row.length; ++j)
				s.append("" + format(row[j])).append(SPACE);
			s.append(NEWLINE);
		}
		model.appendChild( new XMLNode("Transitions")
				.setAttribute("mpeg7:dim", trans.length + " " + trans.length)
				.appendChild(new XMLCharacters(s.toString())));
		
		// set labels for each state
		for (int i=0; i<sound_model.hmm.N; ++i) {
			model.appendChild(new XMLNode("State")
					.appendChild(new XMLNode("Label")
							.appendChild(new XMLNode("Term")
									.setAttribute( "termID", "ModelState_"+i )
									.appendChild(new XMLNode("Name", false)
											.appendChild(new XMLCharacters("State"+i))))));
		}
		
		// needed to be MPEG-7 complient but has no functionality
		model.appendChild( new XMLNode("DescriptorModel")
				.appendChild(new XMLNode("Descriptor")
						.setAttribute("xsi:type", "mpeg7:AudioSpectrumProjectionType")
						.appendChild(new XMLNode("SeriesOfVector")
								.setAttribute("totalNumOfSamples", "1")))
								.appendChild(new XMLNode("Field")
										.appendChild(new XMLCharacters("SeriesOfVector"))));
		
		// write gaussian distributions
		for (int i=0; i<sound_model.hmm.N; ++i) {
			GaussianDistribution gd = sound_model.hmm.getDist(i);
			
			XMLNode node_mean, node_cov_inv;
			model.appendChild(new XMLNode("ObservationDistribution")
					.setAttribute("xsi:type", "mpeg7:GaussianDistributionType")
					.appendChild( (node_mean = new XMLNode("Mean", false))
							.setAttribute("mpeg7:dim", "1 " + gd.getLength()))
							.appendChild( (node_cov_inv = new XMLNode("CovarianceInverse"))
									.setAttribute( "mpeg7:dim", gd.getLength() + " " + gd.getLength())));
			
			float[] center = gd.getCenter();
			float[][] cov_inv = gd.getCovarianceInverse();
			
			StringBuffer buffer = new StringBuffer();
			for (int n=0; n<center.length; ++n)
				buffer.append("" + format(center[n])).append(SPACE);
			node_mean.appendChild(new XMLCharacters(buffer.toString()));
			
			buffer = new StringBuffer();
			for (int n=0; n<cov_inv.length; ++n) {
				float[] row = cov_inv[n];
				for (int m=0; m<row.length; ++m)
					buffer.append("" + format(row[m])).append(SPACE);
				buffer.append(NEWLINE);
			}
			node_cov_inv.appendChild(new XMLCharacters(buffer.toString()));
		}
		
		// write sound label
		model.appendChild(new XMLNode("SoundClassLabel")
				.appendChild(new XMLNode("Name", false)
						.appendChild(new XMLCharacters(sound_model.label))));
		
		// write AudioSpectrumBasis;
		float[][] asb = sound_model.audio_spectrum_basis;
		s = new StringBuffer();
		for (int m=0; m<asb.length; ++m) {
			float[] row = asb[m];
			for (int n=0; n<row.length; ++n)
				s.append(row[n]).append(SPACE);
			s.append(NEWLINE);
		}
		model.appendChild( new XMLNode("SpectrumBasis")
				.setAttribute("loEdge", "" + sound_model.lo_edge)
				.setAttribute("hiEdge", "" + sound_model.hi_edge)
				.setAttribute("octaveResolution",
						"" + getOctaveResolution(sound_model.resolution))
						.appendChild(new XMLNode("SeriesOfVector")
								.setAttribute("hopSize", "PT10N1000F")
								.setAttribute("totalNumOfSamples", "1")
								.appendChild((new XMLNode("Raw"))
										.setAttribute("mpeg7:dim", asb.length + " " + asb[0].length)
										.appendChild(new XMLCharacters(s.toString())))));
	}
	
	private void addAFF( XMLNode audio ) {
		if (setAFF.isEmpty())
			return;
		
		MsgAudioFundamentalFrequency msg = (MsgAudioFundamentalFrequency)
		setAFF.first();
		
		XMLNode fundamental;
		XMLNode confidence;
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute( "xsi:type" , "AudioFundamentalFrequencyType")
				.setAttribute("loLimit", "" + msg.lolimit)
				.setAttribute("hiLimit", "" + msg.hilimit)
				.appendChild( new XMLNode ( SOS )
						.setAttribute( "totalNumOfSamples", "" + setAFF.size() )
						.setAttribute( "hopSize", getHopSize( msg ) )
						.appendChild( fundamental = new XMLNode( "Raw" ) )
						.appendChild( confidence = new XMLNode( "Weight" ) )
				)
		);
		
		StringBuffer sf0 = new StringBuffer( "" );
		StringBuffer sconf = new StringBuffer( "" );
		Iterator i = setAFF.iterator();
		while( i.hasNext() ) {
			msg = (MsgAudioFundamentalFrequency) i.next();
			sf0.append(format(msg.fundfreq)).append(SPACE);
			sconf.append(format(msg.confidence)).append(SPACE);
		}
		fundamental.appendChild( new XMLCharacters(sf0.toString()) );
		confidence.appendChild( new XMLCharacters(sconf.toString()) );
	}
	
	private void addAH ( XMLNode audio ) {
		if (setAH.isEmpty() )
			return;
		MsgAudioHarmonicity msg = (MsgAudioHarmonicity) setAH.first();
		
		XMLNode raw1;
		XMLNode raw2;
		
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute("xsi:type", "AudioHarmonicityType")
				.appendChild( new XMLNode( "HarmonicRatio")
						.appendChild(new XMLNode (SOS)
								.setAttribute("totalNumOfSamples", "" + setAH.size())
								.setAttribute("hopSize", getHopSize( msg ))
								.appendChild(raw1 = new XMLNode("Raw"))
						)
				)
				.appendChild( new XMLNode( "UpperLimitOfHarmonicity" )
						.appendChild( new XMLNode (SOS)
								.setAttribute("totalNumOfSamples", "" + setAH.size())
								.setAttribute("hopSize", getHopSize( msg ))
								.appendChild(raw2 = new XMLNode("Raw"))
						)
				)
		);
		StringBuffer rawhr = new StringBuffer("");
		StringBuffer rawul = new StringBuffer("");
		Iterator i = setAH.iterator();
		while (i.hasNext()) {
			msg = (MsgAudioHarmonicity) i.next();
			rawhr.append(format(msg.harmonicratio)).append(SPACE);
			rawul.append(format(msg.upperlimit)).append(SPACE);
		}
		raw1.appendChild(new XMLCharacters(rawhr.toString()));
		raw2.appendChild(new XMLCharacters(rawul.toString()));
	}
	
	private void addLAT( XMLNode audio) {
		if (setLAT.isEmpty() )
			return;
		MsgLogAttackTime msg = (MsgLogAttackTime)setLAT.first();
		
		XMLNode lat;
		
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute("xsi:type", "LogAttackTimeType")
				.appendChild( lat = new XMLNode("Scalar"))
		);
		
		StringBuffer slat = new StringBuffer("");
		Iterator i = setLAT.iterator();
		while (i.hasNext()) {
			msg = (MsgLogAttackTime)i.next();
			slat.append(format(msg.lat)).append(NEWLINE);
		}
		
		lat.appendChild(new XMLCharacters(slat.toString()));
		
	}
	
	
	private void addHSC( XMLNode audio) {
		if (setHSC.isEmpty() )
			return;
		MsgHarmonicSpectralCentroid msg = (MsgHarmonicSpectralCentroid)setHSC.first();
		
		XMLNode hsc;
		
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute("xsi:type", "HarmonicSpectralCentroidType")
				.appendChild( hsc = new XMLNode("Scalar"))
		);
		
		StringBuffer shsc = new StringBuffer("");
		Iterator i = setHSC.iterator();
		while (i.hasNext()) {
			msg = (MsgHarmonicSpectralCentroid)i.next();
			shsc.append(format(msg.hsc)).append(NEWLINE);
		}
		
		hsc.appendChild(new XMLCharacters(shsc.toString()));
		
	}
	
	private void addHSD( XMLNode audio) {
		if (setHSD.isEmpty() )
			return;
		MsgHarmonicSpectralDeviation msg = (MsgHarmonicSpectralDeviation)setHSD.first();
		
		XMLNode hsd;
		
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute("xsi:type", "HarmonicSpectralDeviationType")
				.appendChild( hsd = new XMLNode("Scalar"))
		);
		
		StringBuffer shsd = new StringBuffer("");
		Iterator i = setHSD.iterator();
		while (i.hasNext()) {
			msg = (MsgHarmonicSpectralDeviation)i.next();
			shsd.append(format(msg.hsd)).append(NEWLINE);
		}
		
		hsd.appendChild(new XMLCharacters(shsd.toString()));
	}
	
	private void addHSS( XMLNode audio) {
		if (setHSS.isEmpty() )
			return;
		MsgHarmonicSpectralSpread msg = (MsgHarmonicSpectralSpread)setHSS.first();
		
		XMLNode hss;
		
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute("xsi:type", "HarmonicSpectralSpreadType")
				.appendChild( hss = new XMLNode("Scalar"))
		);
		
		StringBuffer shss = new StringBuffer("");
		Iterator i = setHSS.iterator();
		while (i.hasNext()) {
			msg = (MsgHarmonicSpectralSpread)i.next();
			shss.append(format(msg.hss)).append(NEWLINE);
		}
		
		hss.appendChild(new XMLCharacters(shss.toString()));
	}
	
	private void addHSV( XMLNode audio) {
		if (setHSV.isEmpty() )
			return;
		MsgHarmonicSpectralVariation msg = (MsgHarmonicSpectralVariation)setHSV.first();
		
		XMLNode hss;
		
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute("xsi:type", "HarmonicSpectralVariationType")
				.appendChild( hss = new XMLNode("Scalar"))
		);
		
		StringBuffer shsv = new StringBuffer("");
		Iterator i = setHSV.iterator();
		while (i.hasNext()) {
			msg = (MsgHarmonicSpectralVariation)i.next();
			shsv.append(format(msg.hsv)).append(NEWLINE);
		}
		
		hss.appendChild(new XMLCharacters(shsv.toString()));
	}
	
	private void addSC( XMLNode audio) {
		if (setSC.isEmpty() )
			return;
		MsgSpectralCentroid msg = (MsgSpectralCentroid)setSC.first();
		
		XMLNode sc;
		
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute("xsi:type", "SpectralCentroidType")
				.appendChild( sc = new XMLNode("Scalar"))
		);
		
		StringBuffer ssc = new StringBuffer("");
		Iterator i = setSC.iterator();
		while (i.hasNext()) {
			msg = (MsgSpectralCentroid)i.next();
			ssc.append(format(msg.spectralCentroid)).append(NEWLINE);
		}
		
		sc.appendChild(new XMLCharacters(ssc.toString()));
	}
	
	private void addTC( XMLNode audio) {
		if (setTC.isEmpty() )
			return;
		MsgTemporalCentroid msg = (MsgTemporalCentroid)setTC.first();
		
		XMLNode tc;
		
		audio.appendChild( new XMLNode( "AudioDescriptor" )
				.setAttribute("xsi:type", "TemporalCentroidType")
				.appendChild( tc = new XMLNode("Scalar"))
		);
		
		StringBuffer stc = new StringBuffer("");
		Iterator i = setTC.iterator();
		while (i.hasNext()) {
			msg = (MsgTemporalCentroid)i.next();
			stc.append(format(msg.temporalCentroid)).append(NEWLINE);
		}
		
		tc.appendChild(new XMLCharacters(stc.toString()));
	}
	
	
	private static String getHopSize( Msg msg ) {
		return new String("PT" + msg.hopsize + "N1000F");
	}
	
	private static String getOctaveResolution(float resolution) {
		if (resolution >=1)
			return ""   + Math.round(resolution);
		else
			return "1/" + Math.round(1.0f / resolution);
	}
}

class SeriesOfVector
extends XMLNode
{
	SeriesOfVector(String hopSize,
			int totalNumOfSamples,
			int vectorSize)
			{
		super("SeriesOfVector");
		setAttribute("hopSize", hopSize);
		setAttribute("totalNumOfSamples", "" + totalNumOfSamples);
		setAttribute("vectorSize", "" + vectorSize);
			}
}

