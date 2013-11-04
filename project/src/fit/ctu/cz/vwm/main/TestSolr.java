package fit.ctu.cz.vwm.main;

import fit.ctu.cz.vwm.dao.Dao;
import fit.ctu.cz.vwm.dao.SolrDao;
import fit.ctu.cz.vwm.dao.SolrFields;
import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class TestSolr {

	public static void main(String[] args) throws Exception {

		Dao<AudioDocumentMap> dao = new SolrDao();

		AudioDocumentMap aDoc = new AudioDocumentMap();
		aDoc.put(SolrFields.AudioFundamentalFrequencyType, 1);
		aDoc.put(SolrFields.AudioSpectrumEnvelopeType, 3.5);
		aDoc.put(SolrFields.band_name, "band name");

		// dao.save(aDoc);

		AudioDocumentMap document = dao.getDocument("8942ccef-c837-47c3-bca6-092fa45d2a96");

		System.out.println("DOC: " + document);

	}
}
