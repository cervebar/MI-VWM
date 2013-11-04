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

	public String getSongText() {
		return songText;
	}

	public void setSongText(String songText) {
		this.songText = songText;
	}

	public String getSongLyrics() {
		return songLyrics;
	}

	public void setSongLyrics(String songLyrics) {
		this.songLyrics = songLyrics;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getLoveFactor() {
		return loveFactor;
	}

	public void setLoveFactor(long loveFactor) {
		this.loveFactor = loveFactor;
	}

	public String getRythm() {
		return rythm;
	}

	public void setRythm(String rythm) {
		this.rythm = rythm;
	}

	public String getSongType() {
		return songType;
	}

	public void setSongType(String songType) {
		this.songType = songType;
	}

	public String getBandName() {
		return bandName;
	}

	public void setBandName(String bandName) {
		this.bandName = bandName;
	}

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
