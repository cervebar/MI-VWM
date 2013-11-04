package fit.ctu.cz.vwm.business;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
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
import fit.ctu.cz.vwm.business.extract.Extractor;
import fit.ctu.cz.vwm.business.extract.InstrumentExtractor;
import fit.ctu.cz.vwm.business.extract.LyricsExtractor;
import fit.ctu.cz.vwm.business.extract.SongTextExtractor;
import fit.ctu.cz.vwm.model.AudioDocument;
import fit.ctu.cz.vwm.model.GenreResult;

public class SimpleBUsiness implements Business {
	List<Extractor> extractors;

	public SimpleBUsiness() {
		extractors = new ArrayList<>();
		extractors.add(new InstrumentExtractor());
		extractors.add(new SongTextExtractor());// need to be before lyrics extractor
		extractors.add(new LyricsExtractor());
	}

	@Override
	public String extract(File mp3File) throws IOException, UnsupportedAudioFileException,
			ParserConfigurationException, TransformerException {
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
		String outputPath = "resources/output/ "
				+ mp3File.getName().substring(0, mp3File.getName().indexOf("."));
		PrintStream fileStream = new PrintStream(new File(outputPath));

		transformer.transform(new DOMSource(mp7), new StreamResult(fileStream));
		return outputPath;
	}

	@Override
	public void extractFeatures(AudioDocument aDoc) {
		for (Extractor ex : extractors) {
			ex.extract(aDoc);
		}
		// tempo
		// genre
		// description
		// duration
		// love_factor
		// singing
		// rhytmus
		// song_type
		// band_name

	}

	@Override
	public GenreResult findGenre(AudioDocument aDoc) {
		//
		return null;
	}

}
