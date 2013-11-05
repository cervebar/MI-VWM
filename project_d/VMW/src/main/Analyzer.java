package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.w3c.dom.Document;

import de.crysandt.audio.AudioInFloat;
import de.crysandt.audio.AudioInFloatSampled;
import de.crysandt.audio.mpeg7audio.Config;
import de.crysandt.audio.mpeg7audio.ConfigDefault;
import de.crysandt.audio.mpeg7audio.Encoder;
import de.crysandt.audio.mpeg7audio.MP7DocumentBuilder;

public class Analyzer {
	AudioInFloatSampled a;
	HashMap<String, ArrayList<String>> map;
	MP7DocumentBuilder mp7;
	EConfig conf;
	Config c;
	File f;

	public Analyzer(AudioInFloatSampled ain, EConfig conf,
			MP7DocumentBuilder mp7doc, File f,
			HashMap<String, ArrayList<String>> map) {
		this.map = map;
		this.mp7 = mp7doc;
		this.a = ain;
		this.conf = conf;
		this.c = new ConfigDefault();
		this.f = f;
		c.enableAll(false);
		this.setConfig(conf.config);
	}

	public void setConfig(ArrayList<String> config) {
		for (String s : config) {
			this.c.setValue(s, "enable", true);
		}
	}

	public ArrayList<String> encode() {
		Encoder encoder = new Encoder(a.getSampleRate(), mp7, c);
		// add 0:00, 0:01, ... output
		// encoder.addTimeElapsedListener(new Ticker(System.err));
		// copy audio signal from source to encoder
		float[] audio;
		while ((audio = a.get()) != null) {
			if (!a.isMono())
				audio = AudioInFloat.getMono(audio);
			encoder.put(audio);
		}
		encoder.flush();

		// get MPEG-7 description
		Document mp7d = null;

		try {
			mp7d = mp7.getDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}

		XMLParser xml = new XMLParser(mp7d, f);
		writeAsInfo(xml);
		ArrayList<String> result = new ArrayList<>();
		for (int i = 0; i < xml.vals.size(); i++) {
			result.add(Double.toString(Calc.MeanValue(xml.vals.get(i))));
		}
		return result;

	}

	public void writeAsInfo(XMLParser xml) {

		//
		try {
			FileWriter fstream = new FileWriter("results/"
					+ f.getName().substring(0, f.getName().length() - 4)
					+ ".info");
			BufferedWriter out = new BufferedWriter(fstream);

			for (int i = 0; i < xml.descriptors.size(); i++) {
				out.write(xml.descriptors.get(i) + ": ");
				double meanval = Calc.MeanValue(xml.vals.get(i));
				out.write(Double.toString(meanval));
				out.write("\n");
				if (!xml.descriptors.get(i).contains("Standard Deviation")) {
					double stdev = Calc.StandardDeviation(xml.vals.get(i),
							meanval);
					out.write("STDev-" + xml.descriptors.get(i) + ": "
							+ Double.toString(stdev));
					out.write("\n");
				}
			}

			// add values from jaudio map

			for (Entry e : map.entrySet()) {

				out.write((String) e.getKey() + ": ");
				ArrayList<String> arr = (ArrayList<String>) e.getValue();
				String b = "";
				for (String s : arr) {
					b += s;
				}
				out.write(Double.toString(Calc.MeanValue(b)));

				out.write("\n");
			}
			out.close();
		} catch (Exception ex) {
			System.out.println("Error while writing info:");
			ex.printStackTrace();
		}
	}
}
