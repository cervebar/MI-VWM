package fit.ctu.cz.vwm.main;

import fit.ctu.cz.vwm.dao.Constants;
import fit.ctu.cz.vwm.dao.Dao;
import fit.ctu.cz.vwm.dao.SolrDao;
import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class TestSolr {

	public static void main(String[] args) throws Exception {

		// testSolrSaveAndGet();
		// testDocumentUpdate();
		// testDocumentUpdateWithoutRemoveing();
		// testSaveOrUpdate();
		// testDeleteDocument();
		fillIndexTest();

	}

	private static void fillIndexTest() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);
		for (int i = 0; i < 10; i++) {
			AudioDocumentMap aDoc = new AudioDocumentMap();
			aDoc.put(Constants.instruments, "drums");
			aDoc.put(Constants.ID_PATH, "testPAth2");

			aDoc.put(Constants.ID_USER_DEFINED, "id" + i);
			if (i % 2 == 0) {
				aDoc.put(Constants.GENRE, "rock");
				if (i % 4 == 0) {
					aDoc.put(Constants.ID_PATH, "hard cafe whoa bleah kvak ");
				} else {
					aDoc.put(Constants.ID_PATH, " cafe whoa hard hard ");
				}
			} else {
				aDoc.put(Constants.GENRE, "pop");
				if (i % 3 == 0) {
					aDoc.put(Constants.ID_PATH, "love money honey ");
				} else {
					aDoc.put(Constants.ID_PATH, " mama uuu u love ");
				}
			}
			dao.save(aDoc);
		}
	}

	private static void testSaveOrUpdate() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);
		AudioDocumentMap aDoc = new AudioDocumentMap();
		aDoc.put(Constants.instruments, "drums");
		aDoc.put(Constants.ID_PATH, "testPAth2");
		aDoc.put(Constants.ID_PATH, "mama ");
		aDoc.put(Constants.ID_USER_DEFINED, "8");
		aDoc.put(Constants.GENRE, "pop");
		dao.saveOrUpdate(aDoc);
	}

	// updates and adds.nothing is removed
	private static void testDocumentUpdateWithoutRemoveing() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);
		AudioDocumentMap update = new AudioDocumentMap();
		// update.put(SolrFields.AudioFundamentalFrequencyType, 2);
		update.put(Constants.instruments, "bass");
		update.put(Constants.ID_PATH, "testPAth");
		// update.put(SolrFields.description, "updated description" + new Date().toString());
		// update.put(SolrFields.song_type, "some type");
		dao.updateDocumentWithouRemoveing(update, "user_def");
	}

	// this changes all fields, those there aren't explicitly mentioned are removed
	private static void testDocumentUpdate() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);
		AudioDocumentMap update = new AudioDocumentMap();
		// update.put(SolrFields.AudioFundamentalFrequencyType, 2);
		update.put(Constants.GENRE, "hardrock");
		dao.updateDocumentWithouRemoveing(update, "c9ce6695-5543-41b6-a2d2-c12f9cd24272");
	}

	private static void testSolrSaveAndGet() throws Exception {
		Dao<AudioDocumentMap> dao = new SolrDao(Constants.LOCALHOST);

		String userDefID = "udfID1";

		AudioDocumentMap aDoc = new AudioDocumentMap();
		aDoc.put(Constants.AudioFundamentalFrequencyType, 1);
		aDoc.put(Constants.band_name, "bandkvak name");
		aDoc.put(Constants.ID_USER_DEFINED, userDefID);
		aDoc.put(Constants.DESCRIPTION, "raw low heavy smile funny blue red green white");

		dao.save(aDoc);

		// AudioDocumentMap document = dao.getDocumentByID("c049b635-4948-4c50-918b-59a184467fc9");
		AudioDocumentMap document = dao.getDocumentByUserDefinedID(userDefID);

		System.out.println("DOC: " + document);
	}

}
