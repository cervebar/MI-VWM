package fit.ctu.cz.vwm.business.extract;

import java.io.File;

import fit.ctu.cz.vwm.dao.Constants;
import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class GroundTruthExtractor implements Extractor {

	@Override
	public void extract(File file, AudioDocumentMap aDoc) throws Exception {
		aDoc.put(Constants.ID_PATH, file.getPath());
		aDoc.put(Constants.name, file.getName());
		aDoc.put(Constants.GENRE, file.getParentFile().getName());
		aDoc.put(Constants.category, Constants.CATEGORY_GROUND_TRUTH);
	}

}
