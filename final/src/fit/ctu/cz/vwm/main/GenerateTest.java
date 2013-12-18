package fit.ctu.cz.vwm.main;

import java.io.File;
import java.io.PrintStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import de.crysandt.audio.AudioInFloat;
import de.crysandt.audio.AudioInFloatSampled;
import de.crysandt.audio.mpeg7audio.Config;
import de.crysandt.audio.mpeg7audio.ConfigDefault;
import de.crysandt.audio.mpeg7audio.Encoder;
import de.crysandt.audio.mpeg7audio.MP7DocumentBuilder;
import de.crysandt.audio.mpeg7audio.Ticker;
import de.crysandt.audio.mpeg7audio.mci.MediaHelper;
import de.crysandt.audio.mpeg7audio.mci.MediaInformation;
import de.crysandt.xml.Namespace;

public class GenerateTest {

	public static void main(String[] args) {
		try {
			// s
			String mp3filePath = "/home/babu/Dropbox/FIT/MI-VWM/MI-VWM/dataset/klasika/braniborsky_koncert.mp3";
			// open audio source
			File mp3File = new File(mp3filePath);
			AudioInFloatSampled audioin = new AudioInFloatSampled(mp3File);

			// create MPEG-7 DocumentBuilder
			MP7DocumentBuilder mp7out = new MP7DocumentBuilder();
			mp7out.addSchemaLocation(Namespace.MPEG7,
					"http://www.ient.rwth-aachen.de/team/crysandt/mpeg7mds/mpeg7ver1.xsd");

			MediaInformation mi = MediaHelper.createMediaInformation();
			MediaHelper.setMediaLocation(mi, mp3File.toURI());
			mp7out.setMediaInformation(mi);

			// create encoder
			Encoder encoder = null;
			// read config from second file
			// Config config = ConfigXML.parse(new FileReader(args[1]));
			Config config = new ConfigDefault();
			config.enableAll(true);
			encoder = new Encoder(audioin.getSampleRate(), mp7out, config);

			// add 0:00, 0:01, ... output
			encoder.addTimeElapsedListener(new Ticker(System.err));

			// copy audio signal from source to encoder
			float[] audio;
			while ((audio = audioin.get()) != null) {
				if (!audioin.isMono())
					audio = AudioInFloat.getMono(audio);
				encoder.put(audio);
			}
			encoder.flush();

			// get MPEG-7 description
			Document mp7 = mp7out.getDocument();

			// initialize output format
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// write MPEG-7 description to file
			PrintStream fileStream = new PrintStream(new File("resources/output/ "
					+ mp3File.getName().substring(0, mp3File.getName().indexOf("."))));

			transformer.transform(new DOMSource(mp7), new StreamResult(fileStream));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}
}
