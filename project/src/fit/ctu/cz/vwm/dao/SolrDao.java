package fit.ctu.cz.vwm.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class SolrDao implements Dao<AudioDocumentMap> {
	HttpSolrServer solrCore = new HttpSolrServer("http://ir-dev.lmcloud.vse.cz/solr/vwm");

	@Override
	public void save(AudioDocumentMap aDoc) throws Exception {
		SolrInputDocument solrDoc = convertToSolrDoc(aDoc);
		solrCore.add(solrDoc);
		UpdateResponse updateResponse = solrCore.commit();
		System.out.println("Commited : " + updateResponse.getStatus());
	}

	private SolrInputDocument convertToSolrDoc(AudioDocumentMap aDoc) {
		SolrInputDocument solrDoc = new SolrInputDocument();

		for (String key : aDoc.keySet()) {
			solrDoc.addField(key, aDoc.get(key));
		}
		return solrDoc;
	}

	@Override
	public AudioDocumentMap getDocument(String id) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setParam("q", "id:\"" + id + "\"");

		QueryResponse rsp = solrCore.query(solrQuery);
		SolrDocument solrDocument = rsp.getResults().get(0);
		AudioDocumentMap aDoc = new AudioDocumentMap();

		for (String key : solrDocument.keySet()) {
			aDoc.put(key, solrDocument.get(key));
		}
		return aDoc;
	}

}
