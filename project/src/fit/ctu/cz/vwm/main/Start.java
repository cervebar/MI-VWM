package fit.ctu.cz.vwm.main;

import java.io.File;

import fit.ctu.cz.vwm.business.Business;
import fit.ctu.cz.vwm.business.SimpleBUsiness;
import fit.ctu.cz.vwm.dao.Dao;
import fit.ctu.cz.vwm.dao.SolrDao;
import fit.ctu.cz.vwm.model.AudioDocument;
import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class Start {

	public static void main(String[] args) throws Exception {
		String mp3filePath = "/home/babu/Dropbox/FIT/MI-VWM/MI-VWM/dataset/klasika/braniborsky_koncert.mp3";
		File mp3File = new File(mp3filePath);

		Business business = new SimpleBUsiness();
		Dao<AudioDocumentMap> dao = new SolrDao();

		// String pathToXmlExtraction;
		// try {
		// pathToXmlExtraction = business.extract(mp3File);
		// } catch (IOException | UnsupportedAudioFileException | ParserConfigurationException
		// | TransformerException e) {
		// e.printStackTrace();
		// return;
		// }

		AudioDocument aDoc = new AudioDocument();
		// aDoc.setPathToXmlExtraction(pathToXmlExtraction);
		aDoc.setName(mp3File.getName().substring(0, mp3File.getName().indexOf(".")));
		aDoc.setPathToMp3File(mp3filePath);

		// business.extractInstruments(aDoc);
		// business.extractText(aDoc);
		// business.extractFeatures(aDoc);

		dao.save(aDoc);

		// MLT approach
		// GenreResult genre = business.findGenre(aDoc);
		// System.out.println(genre.pritnInfo());

		// IS is? approach

		// cluster approach

	}
}
