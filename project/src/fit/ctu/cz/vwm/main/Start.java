package fit.ctu.cz.vwm.main;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import fit.ctu.cz.vwm.business.Business;
import fit.ctu.cz.vwm.business.SimpleBUsiness;
import fit.ctu.cz.vwm.dao.Dao;
import fit.ctu.cz.vwm.dao.SimpleDao;
import fit.ctu.cz.vwm.model.AudioDocument;

public class Start {

	public static void main(String[] args) {
		String mp3filePath = "/home/babu/Dropbox/FIT/MI-VWM/MI-VWM/dataset/klasika/braniborsky_koncert.mp3";
		File mp3File = new File(mp3filePath);

		Business business = new SimpleBUsiness();
		Dao dao = new SimpleDao();

		String pathToXmlExtraction;
		try {
			pathToXmlExtraction = business.extract(mp3File);
		} catch (IOException | UnsupportedAudioFileException | ParserConfigurationException
				| TransformerException e) {
			e.printStackTrace();
			return;
		}

		AudioDocument aDoc = new AudioDocument();
		aDoc.setPathToXmlExtraction(pathToXmlExtraction);

		// business.extractInstruments(aDoc);
		// business.extractText(aDoc);
		business.extractDescriptor(aDoc);
		dao.save(aDoc);

	}
}
