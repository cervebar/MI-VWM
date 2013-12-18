package fit.ctu.cz.vwm.dao;

import fit.ctu.cz.vwm.model.AudioDocumentMap;

public interface Dao<T> {

	T getDocumentByID(String id) throws Exception;

	void save(T doc) throws Exception;

	void updateDocument(T update, String id) throws Exception;

	void updateDocumentWithouRemoveing(AudioDocumentMap update, String id) throws Exception;

	void saveOrUpdate(AudioDocumentMap aDoc) throws Exception;

	AudioDocumentMap getDocumentByUserDefinedID(String idUserDefined) throws Exception;

}
