package fit.ctu.cz.vwm.business.extract;

import java.io.File;

import fit.ctu.cz.vwm.dao.Constants;
import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class QueryTimeExtractor implements Extractor {

	@Override
	public void extract(File file, AudioDocumentMap aDoc) throws Exception {
		aDoc.put(Constants.ID_PATH, file.getPath());
		aDoc.put(Constants.name, file.getName());
		aDoc.put(Constants.category, Constants.CATEGORY_NO_GENRE_EXTRACTED);
	}

}
