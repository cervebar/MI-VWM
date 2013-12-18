package fit.ctu.cz.vwm.dao;

import junit.framework.Assert;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class SolrDao implements Dao<AudioDocumentMap> {
	HttpSolrServer solrCore;

	public SolrDao(String serverCore) {
		this.solrCore = new HttpSolrServer(serverCore);
	}

	@Override
	public void save(AudioDocumentMap aDoc) throws Exception {
		SolrInputDocument solrDoc = convertToSolrDoc(aDoc);
		solrCore.add(solrDoc);
		UpdateResponse updateResponse = solrCore.commit();
		System.out.println("New document saved - response status : " + updateResponse.getStatus());
	}

	@Override
	public void saveOrUpdate(AudioDocumentMap aDoc) throws Exception {
		SolrQuery solrQuery = new SolrQuery(Constants.ID_PATH + ":\"" + aDoc.get(Constants.ID_PATH)
				+ "\"");
		QueryResponse rsp = solrCore.query(solrQuery);
		SolrDocumentList results = rsp.getResults();
		Assert.assertTrue(results.size() == 0 || results.size() == 1);
		if (results.size() == 0) {
			save(aDoc);
		} else {
			String id = (String) results.get(0).get(Constants.id);
			System.out.println("Document id: " + id + " updating...");
			updateDocumentWithouRemoveing(aDoc, id);
		}
	}

	@Override
	public AudioDocumentMap getDocumentByID(String id) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setParam("q", "id:\"" + id + "\"");

		QueryResponse rsp = solrCore.query(solrQuery);
		SolrDocument solrDocument = null;
		AudioDocumentMap aDoc = null;
		if (rsp.getResults().size() != 0) {
			solrDocument = rsp.getResults().get(0);
			aDoc = new AudioDocumentMap();
			for (String key : solrDocument.keySet()) {
				aDoc.put(key, solrDocument.get(key));
			}

		}
		return aDoc;
	}

	@Override
	public AudioDocumentMap getDocumentByUserDefinedID(String idUserDefined) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setParam("q", Constants.ID_USER_DEFINED + ":\"" + idUserDefined + "\"");

		QueryResponse rsp = solrCore.query(solrQuery);
		SolrDocument solrDocument = null;
		AudioDocumentMap aDoc = null;

		if (rsp.getResults().size() != 1) {
			System.out.println("SolrDao WARN: query didn found only one document  res.size:"
					+ rsp.getResults().size());
		}

		if (rsp.getResults().size() != 0) {
			solrDocument = rsp.getResults().get(0);
			aDoc = new AudioDocumentMap();
			for (String key : solrDocument.keySet()) {
				aDoc.put(key, solrDocument.get(key));
			}

		}
		return aDoc;
	}

	public SolrDocument getSolrDocument(String id) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setParam("q", "id:\"" + id + "\"");

		QueryResponse rsp = solrCore.query(solrQuery);
		SolrDocument solrDocument = null;
		if (rsp.getResults().size() != 0) {
			solrDocument = rsp.getResults().get(0);
		}
		return solrDocument;
	}

	// curlQuery="curl $server/solr/update?commit=true -H $CT -d [{\"id\":\"$1\",\"$2\":{\"set\":\"$3\"}}]"
	@Override
	public void updateDocument(AudioDocumentMap aDoc, String id) throws Exception {
		SolrInputDocument solrDoc = convertToSolrDoc(aDoc);
		if (solrDoc.get(Constants.id) == null) {
			solrDoc.addField(Constants.id, id);
		}
		UpdateRequest req = new UpdateRequest();
		SolrInputDocument mySolrInputDocument = new SolrInputDocument(solrDoc);
		req.add(mySolrInputDocument);
		req.setCommitWithin(10000);
		UpdateResponse updateResponse = req.process(solrCore);
		System.out.println("Updated with removeing - reponse code: " + updateResponse.getStatus());
	}

	@Override
	public void updateDocumentWithouRemoveing(AudioDocumentMap update, String id) throws Exception {
		SolrDocument docToUpdate = getSolrDocument(id);
		SolrInputDocument solrInputDocument = convertUpdateSolrDoc(docToUpdate, update);
		UpdateRequest req = new UpdateRequest();
		req.add(solrInputDocument);
		req.setCommitWithin(10000);
		UpdateResponse updateResponse = req.process(solrCore);
		System.out.println("Updated - reponse code: " + updateResponse.getStatus());
	}

	// HELP METHODS ------------------------------------------------
	private SolrInputDocument convertToSolrDoc(AudioDocumentMap aDoc) {
		SolrInputDocument solrDoc = new SolrInputDocument();

		for (String key : aDoc.keySet()) {
			solrDoc.addField(key, aDoc.get(key));
		}
		return solrDoc;
	}

	private SolrInputDocument convertUpdateSolrDoc(SolrDocument docToUpdate,
			AudioDocumentMap updateDoc) {
		SolrInputDocument solrInDoc = new SolrInputDocument();

		for (String key : docToUpdate.keySet()) {
			solrInDoc.addField(key, docToUpdate.getFieldValue(key));
		}
		for (String key : updateDoc.keySet()) {
			if (solrInDoc.get(key) != null) {
				solrInDoc.setField(key, updateDoc.get(key));
			} else {
				solrInDoc.addField(key, updateDoc.get(key));
			}
		}
		return solrInDoc;
	}

}
