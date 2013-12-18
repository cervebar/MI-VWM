package fit.ctu.cz.vwm.business.extract;

import java.io.File;

import fit.ctu.cz.vwm.model.AudioDocumentMap;

public interface Extractor {

	public void extract(File file, AudioDocumentMap aDoc) throws Exception;

}
