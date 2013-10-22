
package main;

import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import de.crysandt.audio.AudioInFloatSampled;
import de.crysandt.audio.mpeg7audio.MP7DocumentBuilder;
import de.crysandt.audio.mpeg7audio.mci.MediaHelper;
import de.crysandt.audio.mpeg7audio.mci.MediaInformation;
import de.crysandt.xml.Namespace;



public class Main {

	public static void main(String[] args) throws Exception {
	
		FileManager fm = new FileManager("dataset");
		ArrayList<ArrayList<String>> values = new ArrayList<>();
		values.add(XLSHeaders());
		EConfig conf = new EConfig();
		conf.add("AudioTempoType"); // not working
		//conf.add("HarmonicPeaks"); // not working + error 
		//conf.add("SignalEnvelope"); // not working
		//conf.add("Click"); // not working
		//conf.add("DigitalClip"); // not working
		//conf.add("AudioWaveForm"); // display wave // not working
		//conf.add("SampleHold"); // complicated
	
	
		// general //
		//conf.add("AudioPower"); // quick summary of signal
	
		//conf.add("AudioSpectrumEnvelope"); // short term power spectrum, display, synthesize, gpsearch
		//conf.add("AudioSpectrumCentroidSpread",2); // dominated by low/high freq? , 2 vals
	
		//conf.add("SoundModel",25); // pracuje dlouho, 25 vals
		//conf.add("BackgroundNoiseLevel"); //
		//conf.add("DcOffset");
		//conf.add("BandWidth");
	
		//conf.add("AudioSpectrumFlatness"); // presence of tonal components
			
		// periodic or quasi-periodic signals
		//conf.add("AudioFundamentalFrequency"); // pitch tracking, confidence measure
		//conf.add("AudioHarmonicity",2); // distinction between harmonic/inharmonic/nonharmonic spectrum // 2 vals
	
		// tone color or tone quality, temporal char. of sound segments
		//conf.add("LogAttackTime"); // from 0 to max level sound
		//conf.add("TemporalCentroid"); // where in time is energy focused
			
		// spectral features in linear-freq state
		//conf.add("SpectralCentroid"); // distinguishing musical instruments timbres
		//conf.add("HarmonicSpectralCentroid"); //
		//conf.add("HarmonicSpectralDeviation"); // hazi error
		//conf.add("HarmonicSpectralSpread"); //
		//conf.add("HarmonicSpectralVariation"); //
			
		// low-dimensional projections of a spectral space to aid compactness and recognition, 2vals
		//conf.add("AudioSpectrumBasisProjection",2); //
		//// AudioSpectrumBasis:a series of (time-varying / statistically independent) basis functions derived from the singular value decomposition of a normalized power spectrum.
		//// AudioSpectrumProjection: low-d features of a spectrum after projection upon a reduced rank basis				
		for(File f : fm.files){
			try {				
				
				AudioInFloatSampled audioin = new AudioInFloatSampled(f);
				MP7DocumentBuilder mp7out = new MP7DocumentBuilder();
				mp7out.addSchemaLocation(Namespace.MPEG7,"http://www.ient.rwth-aachen.de/team/crysandt/mpeg7mds/mpeg7ver1.xsd");

				MediaInformation mi = MediaHelper.createMediaInformation();
				MediaHelper.setMediaLocation(mi, f.toURI());
				mp7out.setMediaInformation(mi);
		
				Analyzer a = new Analyzer(audioin,conf,mp7out,f);
				ArrayList<String> v = a.encode();
				v.add(0,f.getName());
				values.add(v);
			} catch (UnsupportedAudioFileException ex) {
					System.out.println("Cant read audiofile:" + f.getName());
			}
		}
		XLSMaker.createFromInfo();
	}
	
	public static ArrayList<String> XLSHeaders(){
		ArrayList<String> headers = new ArrayList<>();
		headers.add("filename");
		headers.add("AudioFundamentalFrequency");
		headers.add("HarmonicRatio");
		headers.add("UpperLimitOfHarmonicity");
		headers.add("AudioPowerType");
		headers.add("AudioSpectrumBasisType");
		headers.add("AudioSpectrumProjectionType");
		headers.add("AudioSpectrumCentroidType");
		headers.add("AudioSpectrumEnvelopeType");
		headers.add("AudioSpectrumFlatnessType");
		headers.add("AudioSpectrumSpreadType");
		headers.add("LogAttackTimeType");
		headers.add("SpectralCentroidType");
		headers.add("TemporalCentroidType");
		headers.add("HarmonicSpectralCentroidType");
		headers.add("HarmonicSpectralDeviationType");
		headers.add("HarmonicSpectralSpreadType");
		headers.add("HarmonicSpectralVariationType");
		headers.add("BackgroundNoiseLevel");
		headers.add("DcOffset");
		headers.add("BandWidth"); 
		return headers;
	}
}
	

