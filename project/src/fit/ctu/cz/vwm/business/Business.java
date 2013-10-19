package fit.ctu.cz.vwm.business;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import fit.ctu.cz.vwm.model.AudioDocument;
import fit.ctu.cz.vwm.model.GenreResult;

public interface Business {

	String extract(File mp3File) throws IOException, UnsupportedAudioFileException,
			ParserConfigurationException, TransformerConfigurationException, TransformerException;

	void extractFeatures(AudioDocument aDoc);

	GenreResult findGenre(AudioDocument aDoc);

}
