package main;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
		// conf.add("AudioTempoType"); // not working
		// conf.add("HarmonicPeaks"); // not working + error
		// conf.add("SignalEnvelope"); // not working
		// conf.add("Click"); // not working
		// conf.add("DigitalClip"); // not working
		// conf.add("AudioWaveForm"); // display wave // not working
		// conf.add("SampleHold"); // complicated

		// general //
		conf.add("AudioPower"); // quick summary of signal
		conf.add("AudioSpectrumEnvelope"); // short term power spectrum,

		conf.add("AudioSpectrumCentroidSpread", 2); // dominated by low/high
		conf.add("SoundModel", 25); // pracuje dlouho, 25 vals
		conf.add("BackgroundNoiseLevel"); //
		conf.add("DcOffset");
		conf.add("BandWidth");
		conf.add("AudioSpectrumFlatness"); // presence of tonal components

		// periodic or quasi-periodic signals
		conf.add("AudioFundamentalFrequency"); // pitch tracking, confidence
		conf.add("AudioHarmonicity", 2); // distinction between

		// tone color or tone quality, temporal char. of sound segments
		conf.add("LogAttackTime"); // from 0 to max level sound
		conf.add("TemporalCentroid"); // where in time is energy focused

		// spectral features in linear-freq state
		conf.add("SpectralCentroid"); // distinguishing musical instruments
		conf.add("HarmonicSpectralCentroid"); //
		conf.add("HarmonicSpectralDeviation"); // hazi error
		conf.add("HarmonicSpectralSpread"); //
		conf.add("HarmonicSpectralVariation"); //

		// low-dimensional projections of a spectral space to aid compactness
		conf.add("AudioSpectrumBasisProjection", 2); //

		for (File source : fm.files) {

			// ************** convert to wav ******************* //
			File target = new File(source.getName()
					.substring(0, source.getName().indexOf("."))
					.replaceAll(" ", "")
					+ ".wav");

			AudioAttributes audio = new AudioAttributes();
			audio.setCodec("pcm_s16le");
			EncodingAttributes attrs = new EncodingAttributes();
			attrs.setFormat("wav");
			attrs.setAudioAttributes(audio);
			Encoder enc = new Encoder();
			enc.encode(source, target, attrs);
			// use jaudio by cmdline{

			String resfile = source.getName().substring(0,
					source.getName().indexOf("."))
					+ ".xml";
			String command = "cmd /c java -jar jaudio.jar -s settings.xml "
					+ "res" + " " + target.getAbsolutePath();
			System.out.println(command);
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			System.out.println("jAudio finished!");

			// transform jaudio output to descriptions and values
			File fv = new File("resFV.xml");
			File fk = new File("resFK.xml");
			HashMap<String, ArrayList<String>> map = JAudioParser.parseFV(fv);

			// extract mpeg7
			try {

				AudioInFloatSampled audioin = new AudioInFloatSampled(source);
				MP7DocumentBuilder mp7out = new MP7DocumentBuilder();
				mp7out.addSchemaLocation(Namespace.MPEG7,
						"http://www.ient.rwth-aachen.de/team/crysandt/mpeg7mds/mpeg7ver1.xsd");

				MediaInformation mi = MediaHelper.createMediaInformation();
				MediaHelper.setMediaLocation(mi, source.toURI());
				mp7out.setMediaInformation(mi);

				Analyzer a = new Analyzer(audioin, conf, mp7out, source, map);
				ArrayList<String> v = a.encode();
				v.add(0, source.getName());
				values.add(v);
			} catch (UnsupportedAudioFileException ex) {
				System.out.println("Cant read audiofile:" + source.getName());
			}

			// file cleanup
			target.delete();
			fk.delete();
			fv.delete();

		}
		XLSMaker.createFromInfo();

	}

	public static ArrayList<String> XLSHeaders() {
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
