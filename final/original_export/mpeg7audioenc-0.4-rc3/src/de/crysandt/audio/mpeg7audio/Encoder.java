/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.audio.mpeg7audio;

import it.univpm.deit.audio.*;

import java.io.*;
import java.util.*;

import javax.sound.sampled.AudioFormat;

import de.crysandt.audio.*;
import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.util.Debug;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class Encoder
		extends MsgSpeaker
{
	private Set time_elapsed_listener = Collections.synchronizedSet(new HashSet());

	/**
	 * @param samplerate samplerate of the audio source
	 * @param listener MP7Writer which creates the mpeg-7 description of the
	 *                 messages extracted and sent by the Encoder
	 * @param config configuration to turn on/off certain descriptors and
	 *               description schemas. Parameters used to configure a
	 *               descriptor are also passed by this parameter (@see
	 *               for details)
	 */
	public Encoder(
			float samplerate,
			MsgListener listener,
			Config config) 
	{
		MsgListenerMultiplexer msgmultiplexer = new MsgListenerMultiplexer() {
			public void flush() { } // dont't flush() the listeners!
		};
		msgmultiplexer.addMsgListener(listener);

		Resizer resizer = new Resizer(
				  samplerate, config.getInt("Resizer", "HopSize"));

		Resizer attresizer = new Resizer(
				  samplerate, config.getInt("AudioTempoType", "ATTHopsize"));

		addMsgListener(resizer);
		addMsgListener(attresizer);

		resizer.addMsgListener(new MsgListener() {
			public void receivedMsg(Msg msg) {
				if (!time_elapsed_listener.isEmpty() && msg instanceof MsgResizer) {
					MsgResizer msg_resizer = (MsgResizer) msg;
					for (Iterator i = time_elapsed_listener.iterator(); i.hasNext();)
						((TimeElapsedListener) i.next()).timeElapsed(msg_resizer.time);
				}
			}
		});

		resizer.addMsgListener(msgmultiplexer);

		if (config.getBoolean("AudioWaveform", "enable")) {
			AudioWaveform awf = new AudioWaveform();
			resizer.addMsgListener(awf);
			awf.addMsgListener(msgmultiplexer);
		}	
			
	    if (config.getBoolean("DigitalClip", "enable")){
		    DigitalClip dc = new DigitalClip(samplerate);
			resizer.addMsgListener(dc);
			dc.addMsgListener(msgmultiplexer);	
		}
		
		if (config.getBoolean("DigitalZero", "enable")) {
			DigitalZero dz = new DigitalZero(samplerate);
			resizer.addMsgListener(dz);
			dz.addMsgListener(msgmultiplexer);
		}
		if (config.getBoolean("SampleHold", "enable")) {
			SampleHold s_h = new SampleHold(samplerate);
			resizer.addMsgListener(s_h);
			s_h.addMsgListener(msgmultiplexer);
		}
		if (config.getBoolean("Click", "enable")) {
			Click ck = new Click(samplerate);
			resizer.addMsgListener(ck);
			ck.addMsgListener(msgmultiplexer);
		}
		if (config.getBoolean("BackgroundNoiseLevel", "enable")) {
		    BackgroundNoiseLevel bnl = new BackgroundNoiseLevel(samplerate);
			resizer.addMsgListener(bnl);
			bnl.addMsgListener(msgmultiplexer);
		}
		if (config.getBoolean("DcOffset", "enable")) {
		    DcOffset dcof = new DcOffset();
			resizer.addMsgListener(dcof);
			dcof.addMsgListener(msgmultiplexer);
		}
		
		

		if (config.getBoolean("TemporalCentroid", "enable") ||
			config.getBoolean("LogAttackTime", "enable")) {
			int windowlength = config.getInt("SignalEnvelope", "windowlength");
			int windowslide = config.getInt("SignalEnvelope", "windowslide");
			SignalEnvelope sEnv = new SignalEnvelope(windowlength, windowslide);
			resizer.addMsgListener(sEnv);

			if (config.getBoolean("LogAttackTime", "enable")) {
				LogAttackTime lat =
						new LogAttackTime(
								samplerate,
								config.getFloat("LogAttackTime",
								"threshold"));
				sEnv.addMsgListener(lat);
				lat.addMsgListener(msgmultiplexer);
			}

			if (config.getBoolean("TemporalCentroid", "enable")) {
				TemporalCentroid tc = new TemporalCentroid(samplerate);
				sEnv.addMsgListener(tc);
				tc.addMsgListener(msgmultiplexer);
			}
		}

		if (config.getBoolean("AudioFundamentalFrequency", "enable")) {
			AudioFundamentalFrequency aff = new AudioFundamentalFrequency(
					config.getInt("Resizer", "HopSize"),
					samplerate,
					config.getFloat("AudioFundamentalFrequency", "lolimit"),
					config.getFloat("AudioFundamentalFrequency", "hilimit"));
			resizer.addMsgListener(aff);
			aff.addMsgListener(msgmultiplexer);
		}

		if (config.getBoolean("AudioPower", "enable")) {
			boolean db_scale = config.getBoolean("AudioPower", "logScale");
			AudioPower ap = new AudioPower(db_scale);
			resizer.addMsgListener(ap);
			ap.addMsgListener(msgmultiplexer);
		}

		if (config.getBoolean("AudioTempoType", "enable")) {
			AudioTempoType att = new AudioTempoType(config.getInt(
					"AudioTempoType", "ATTHopsize"),
					config.getInt("AudioTempoType", "loLimit"),
					config.getInt("AudioTempoType", "hiLimit"),
					samplerate);
			attresizer.addMsgListener(att);
			att.addMsgListener(msgmultiplexer);
		}

		if (config.getBoolean("AudioSpectrumCentroidSpread", "enable") ||
			config.getBoolean("AudioSpectrumDistribution", "enable") ||
			config.getBoolean("AudioSpectrumEnvelope", "enable") ||
			config.getBoolean("AudioSpectrumFlatness", "enable") ||
			config.getBoolean("AudioSpectrumBasisProjection", "enable") ||
			config.getBoolean("SoundModel", "enable") ||
			config.getBoolean("AudioFundamentalFrequency", "enable") ||
			config.getBoolean("AudioHarmonicity", "enable") ||
			config.getBoolean("HarmonicSpectralCentroid", "enable") ||
			config.getBoolean("HarmonicSpectralDeviation", "enable") ||
			config.getBoolean("HarmonicSpectralSpread", "enable") ||
			config.getBoolean("HarmonicSpectralVariation", "enable") ||
			config.getBoolean("SpectralCentroid", "enable") ||
			config.getBoolean("AudioSignature", "enable") ||
			config.getBoolean("BandWidth", "enable") ||
			config.getBoolean("Silence", "enable"))
		{
			AudioSpectrum as = new AudioSpectrum(samplerate);
			resizer.addMsgListener(as);

			if (config.getBoolean("AudioFundamentalFrequency", "enable") ||
				config.getBoolean("AudioHarmonicity", "enable") ||
				config.getBoolean("HarmonicSpectralCentroid", "enable") ||
				config.getBoolean("HarmonicSpectralDeviation", "enable") ||
				config.getBoolean("HarmonicSpectralSpread", "enable") ||
				config.getBoolean("HarmonicSpectralVariation", "enable"))
			{
				AudioFundamentalFrequency aff = new AudioFundamentalFrequency(
						config.getInt("Resizer", "HopSize"),
						samplerate,
						config.getFloat("AudioFundamentalFrequency", "lolimit"),
						config.getFloat("AudioFundamentalFrequency", "hilimit"));
				resizer.addMsgListener(aff);
				
				if (config.getBoolean("AudioFundamentalFrequency", "enable"))
					aff.addMsgListener(msgmultiplexer);
				
				if (config.getBoolean("AudioHarmonicity", "enable")) {
					AudioHarmonicity ah = new AudioHarmonicity();
					aff.addMsgListener(ah);
					as.addMsgListener(ah);
					ah.addMsgListener(msgmultiplexer);
				}

				if (config.getBoolean("HarmonicSpectralCentroid", "enable") ||
					config.getBoolean("HarmonicSpectralDeviation", "enable") ||
					config.getBoolean("HarmonicSpectralSpread", "enable") ||
					config.getBoolean("HarmonicSpectralVariation", "enable")) 
				{
					float nonHarmonicity = config.getFloat(
							"HarmonicPeaks", "nonHarmonicity");
					float threshold = config.getFloat(
							"HarmonicPeaks", "threshold");
					HarmonicPeaks hp = new HarmonicPeaks(
							nonHarmonicity, threshold);
					aff.addMsgListener(hp);
					as.addMsgListener(hp);

					if (config.getBoolean("HarmonicSpectralCentroid", "enable") ||
						config.getBoolean("HarmonicSpectralSpread", "enable"))
					{
						HarmonicSpectralCentroidSpread hscs = 
							new HarmonicSpectralCentroidSpread();
						hp.addMsgListener(hscs);
						resizer.addMsgListener(hscs);
						hscs.addMsgListener(msgmultiplexer);
					}

					if (config.getBoolean("HarmonicSpectralDeviation", "enable")) {
						HarmonicSpectralDeviation hsd = 
							new HarmonicSpectralDeviation();
						hp.addMsgListener(hsd);
						hsd.addMsgListener(msgmultiplexer);
						resizer.addMsgListener(hsd);
					}
					
					if (config.getBoolean("HarmonicSpectralVariation", "enable")) {
						HarmonicSpectralVariation hsv = 
							new HarmonicSpectralVariation();
						hp.addMsgListener(hsv);
						hsv.addMsgListener(msgmultiplexer);
						resizer.addMsgListener(hsv);
					}
				}
			}

			if (config.getBoolean("AudioSpectrumCentroidSpread", "enable")) {
				AudioSpectrumCentroidSpread ascs = new
						AudioSpectrumCentroidSpread();
				as.addMsgListener(ascs);
				ascs.addMsgListener(msgmultiplexer);
			}

			if (config.getBoolean("Silence", "enable")) {
				Silence si = new Silence(
						config.getInt("Silence", "min_dur"),
						config.getInt("Silence", "levelofref"));
				resizer.addMsgListener(si);
				si.addMsgListener(msgmultiplexer);
				as.addMsgListener(si);
				si.addMsgListener(msgmultiplexer);
			}
			if (config.getBoolean("BandWidth", "enable")) {
				BandWidth bwd = new BandWidth(samplerate);
				as.addMsgListener(bwd);
				resizer.addMsgListener(bwd);
				bwd.addMsgListener(msgmultiplexer);
			}
			

			if (config.getBoolean("AudioSpectrumEnvelope", "enable") ||
				config.getBoolean("AudioSpectrumDistribution", "enable") ||
				config.getBoolean("AudioSpectrumBasisProjection", "enable") ||
				config.getBoolean("SoundModel", "enable")) 
			{
				AudioSpectrumEnvelope ase = new AudioSpectrumEnvelope(
						config.getFloat("AudioSpectrumEnvelope", "resolution"),
						config.getFloat("AudioSpectrumEnvelope", "loEdge"),
						config.getFloat("AudioSpectrumEnvelope", "hiEdge"),
						config.getBoolean("AudioSpectrumEnvelope", "dbScale"),
						config.getString("AudioSpectrumEnvelope", "normalize"));
				as.addMsgListener(ase);

				if (config.getBoolean("AudioSpectrumEnvelope", "enable"))
					ase.addMsgListener(msgmultiplexer);
				
				if (config.getBoolean("AudioSpectrumDistribution", "enable")) {
					AudioSpectrumDistribution asd = new AudioSpectrumDistribution();
					ase.addMsgListener(asd);
					asd.addMsgListener(msgmultiplexer);
				}
					

				if (config.getBoolean("AudioSpectrumBasisProjection", "enable") ||
					config.getBoolean("SoundModel", "enable")) {
					AudioSpectrumBasisProjection asbp =
							new AudioSpectrumBasisProjection(
									config.getInt(
											"AudioSpectrumBasisProjection",
											"frames"),
									config.getInt(
											"AudioSpectrumBasisProjection",
									"numic"));
					ase.addMsgListener(asbp);

					if (config.getBoolean("AudioSpectrumBasisProjection",
										  "enable"))
						asbp.addMsgListener(msgmultiplexer);

					if (config.getBoolean("SoundModel", "enable")) {
						if (config.getInt("AudioSpectrumBasisProjection", "frames") > 0)
							throw new IllegalArgumentException(
									"Parameter AudioSpectrumBasisProjection->frames must " +
									"be 0 to use SoundModel");

						SoundModel sound_model = new SoundModel(
								config.getInt("SoundModel", "numberOfStates"),
								config.getString("SoundModel", "label"));
						asbp.addMsgListener(sound_model);
						sound_model.addMsgListener(msgmultiplexer);
					}
				}
			}

			if (config.getBoolean("AudioSpectrumFlatness", "enable") ||
				config.getBoolean("AudioSignature", "enable")) {
				float lo = config.getFloat("AudioSpectrumFlatness", "loEdge");
				float hi = config.getFloat("AudioSpectrumFlatness", "hiEdge");
				AudioSpectrumFlatness asf = new AudioSpectrumFlatness(
						samplerate, lo, hi);
				as.addMsgListener(asf);
				if (config.getBoolean("AudioSpectrumFlatness", "enable"))
					asf.addMsgListener(msgmultiplexer);

				if (config.getBoolean("AudioSignature", "enable")) {
					AudioSignature audiosignature = new AudioSignature(
							config.getInt("AudioSignature", "decimation"));
					asf.addMsgListener(audiosignature);
					audiosignature.addMsgListener(msgmultiplexer);
				}
			}

			if (config.getBoolean("SpectralCentroid", "enable")) {
				SpectralCentroid sC = new SpectralCentroid(samplerate);
				as.addMsgListener(sC);
				sC.addMsgListener(msgmultiplexer);
				resizer.addMsgListener(sC);
			}
		}

		// remove resizers which are not used
		if (resizer.getNumberOfListeners() <= 1) {
			resizer.removeMsgListener(msgmultiplexer);
			removeMsgListener(resizer);
		}

		if (attresizer.getNumberOfListeners() == 0)
			removeMsgListener(attresizer);
	}

	public Encoder(float samplerate, MsgListener listener) {
		this(samplerate, listener, new ConfigDefault());
	}

	public void addTimeElapsedListener(TimeElapsedListener listener) {
		if (listener != null)
			time_elapsed_listener.add(listener);
	}

	public void removeTimeElapsedListener(TimeElapsedListener listener) {
		time_elapsed_listener.remove(listener);
	}

	public void put(float[] signal) {
		send(new MsgRawSignal(signal));
	}

	public void flush() {
		//actual data will be filled by the resizer
		send(new MsgEndOfSignal(0, 0));
		super.flush();
		
		// drop all listeners;
		listeners.clear();
	}
	
	/**
	 * @deprecated Please use the main function of the MP7DocumentBuilder instead
	 */
	public static void main(String[] args) {

		if ((args.length == 0) || (args.length > 2)) {
			System.err.println(
					"Java MPEG-7 Audio Encoder " +
					"(http://sourceforge.net/projects/mpeg7audioenc)");
			System.err.println(
					"Usage: java de.crysandt.audio.mpeg7audio.Encoder " +
					"{audio.wav} [config.xml]");
			return;
		}

		try {
			// open audio source
			File file = new File(args[0]);
			AudioInFloatSampled audioin = new AudioInFloatSampled(file);

			// gather information about audio file
			MP7MediaInformation media_info = new MP7MediaInformation();
			media_info.setFileSize(file.length());

			AudioFormat format = audioin.getSourceFormat();
			media_info.setSample(
					format.getSampleRate(),
					format.getSampleSizeInBits());
			media_info.setNumberOfChannels(audioin.isMono() ? 1 : 2);

			// create mpeg-7 writer
			MP7Writer mp7writer = new MP7Writer();
			mp7writer.setMediaInformation(media_info);

			// create encoder
			Encoder encoder = null;
			if (args.length == 2) {
				Config config = new ConfigDefault();
				config.enableAll(false);
				encoder = new Encoder(
						audioin.getSampleRate(),
						mp7writer,
						config = ConfigXML.parse(new FileReader(args[1]),
												 config));
				assert Debug.println(System.err, config.toString());
			} else {
				encoder = new Encoder(audioin.getSampleRate(), mp7writer);
			}
			encoder.addTimeElapsedListener(new Ticker(System.err));

			// copy audio signal from source to encoder
			long oldtime = System.currentTimeMillis();
			float[] audio;
			while ((audio = audioin.get()) != null) {
			    if (!audioin.isMono()) 
			        audio = AudioInFloat.getMono(audio);
				encoder.put(audio);
			}
			encoder.flush();
			System.err.println(
					"Extraction Time (ms): " +
					(System.currentTimeMillis() - oldtime));

			// print MPEG-7 description to standard out
			String mp7 = mp7writer.toString();
			System.out.println(mp7);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit( -1);
		}
	}
}
