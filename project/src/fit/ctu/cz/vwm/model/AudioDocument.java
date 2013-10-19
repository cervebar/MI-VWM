package fit.ctu.cz.vwm.model;

import java.util.HashSet;
import java.util.Set;

public class AudioDocument {

	String id;
	String pathToXmlExtraction;
	String name;
	String pathToMp3File;
	Set<String> instruments;
	String songText;
	String songLyrics;
	String genre;
	long duration;// in seconds
	String description;
	long loveFactor;
	String rythm;
	String songType;
	String bandName;

	public AudioDocument() {
		instruments = new HashSet<>();
	}

	public void addInstrument(String instrument) {
		instruments.add(instrument);
	}

	public Set<String> getInstruments() {
		return instruments;
	}

	public void setInstruments(Set<String> instruments) {
		this.instruments = instruments;
	}

	public String getPathToMp3File() {
		return pathToMp3File;
	}

	public void setPathToMp3File(String pathToMp3File) {
		this.pathToMp3File = pathToMp3File;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPathToXmlExtraction() {
		return pathToXmlExtraction;
	}

	public void setPathToXmlExtraction(String pathToXmlExtraction) {
		this.pathToXmlExtraction = pathToXmlExtraction;
	}

}
