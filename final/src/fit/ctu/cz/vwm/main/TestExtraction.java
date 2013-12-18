package fit.ctu.cz.vwm.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fit.ctu.cz.vwm.business.extract.AudioDescriptorExtractor;
import fit.ctu.cz.vwm.business.extract.Extractor;
import fit.ctu.cz.vwm.business.extract.GroundTruthExtractor;
import fit.ctu.cz.vwm.business.extract.QueryTimeExtractor;
import fit.ctu.cz.vwm.dao.Constants;
import fit.ctu.cz.vwm.dao.Dao;
import fit.ctu.cz.vwm.dao.SolrDao;
import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class TestExtraction {

	public static void main(String[] args) throws Exception {
		// extractGroundTruth();
		// extractInQueryTime();
		testAudioExtractor();
	}

	private static void testAudioExtractor() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);
		List<Extractor> extractors = new ArrayList<>();
		extractors.add(new AudioDescriptorExtractor());
		AudioDocumentMap aDoc = new AudioDocumentMap();
		File audioFile = new File("resources/example");
		// extract features
		for (Extractor ex : extractors) {
			ex.extract(audioFile, aDoc);
		}

		aDoc.printInfo();

		aDoc.put(Constants.DESCRIPTION, "fast hard loud hard crazy rough ");
		dao.saveOrUpdate(aDoc);
		// sout id
		System.out.println("Adoc path: " + aDoc.get(Constants.ID_PATH));
	}

	private static void extractInQueryTime() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);

		List<Extractor> extractors = new ArrayList<>();
		extractors.add(new QueryTimeExtractor());

		AudioDocumentMap aDoc = new AudioDocumentMap();
		// File audioFile = new File("test/braniborsky_koncert.mp3");
		File audioFile = new File("test/EEPogo.mp3");
		// extract features
		for (Extractor ex : extractors) {
			ex.extract(audioFile, aDoc);
		}
		aDoc.put(Constants.DESCRIPTION, "fast hard loud hard crazy rough ");
		dao.saveOrUpdate(aDoc);
		// sout id
		System.out.println("Adoc path: " + aDoc.get(Constants.ID_PATH));
	}

	private static void extractGroundTruth() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);

		// prepare extractors
		List<Extractor> extractors = new ArrayList<>();
		extractors.add(new GroundTruthExtractor());
		// - textual
		// - instruments
		// - rhythm
		// - duration
		// - MPEG7
		// tempo
		// description
		// duration
		// love_factor
		// singing
		// song_type
		// band_name

		// extractors.add(new InstrumentExtractor());
		// extractors.add(new SongTextExtractor());// need to be before lyrics extractor
		// extractors.add(new LyricsExtractor());
		// extractors.add(new MPeg7Extractor());

		// INDEX
		// load files
		File genreFolder = new File("dataset/dechovka");

		for (File audioFile : genreFolder.listFiles()) {
			AudioDocumentMap aDoc = new AudioDocumentMap();
			// extract features
			for (Extractor ex : extractors) {
				ex.extract(audioFile, aDoc);
			}
			// aDoc.put(Constants.description, "fast hard loud hard crazy rough ");
			aDoc.put(Constants.DESCRIPTION, "slow nice long quiet violin");
			// aDoc.put(Constants.description, "loud crazy");
			// save
			dao.saveOrUpdate(aDoc);
		}
	}
}
