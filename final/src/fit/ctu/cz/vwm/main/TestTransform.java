package fit.ctu.cz.vwm.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fit.ctu.cz.vwm.business.extract.DEscriptionExtractor;
import fit.ctu.cz.vwm.business.extract.Extractor;
import fit.ctu.cz.vwm.dao.Constants;
import fit.ctu.cz.vwm.dao.Dao;
import fit.ctu.cz.vwm.dao.SolrDao;
import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class TestTransform {
	public static void main(String[] args) throws Exception {
		// extractGroundTruth();
		// extractInQueryTime();
		testAudioExtractor();
	}

	private static void testAudioExtractor() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);
		List<Extractor> extractors = new ArrayList<>();

		extractors.add(new DEscriptionExtractor());
		AudioDocumentMap aDoc = new AudioDocumentMap();
		File audioFile = new File("resources/example");
		File infoFile = new File("result.info");

		// extract features
		for (Extractor ex : extractors) {
			ex.extract(audioFile, aDoc);
		}

		aDoc.printInfo();

		// aDoc.put(Constants.description, "fast hard loud hard crazy rough ");
		// dao.saveOrUpdate(aDoc);
		// sout id
		System.out.println("Adoc path: " + aDoc.get(Constants.ID_PATH));
	}

}
