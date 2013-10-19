package fit.ctu.cz.vwm.dao;

import fit.ctu.cz.vwm.model.AudioDocument;

public interface Dao {

	AudioDocument getAudioDocument(String id);

	void save(AudioDocument aDoc);

	String getUniqueIDForAudioDocument();

}
