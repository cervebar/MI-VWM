package fit.ctu.cz.vwm.business.search.mlt;

import java.io.IOException;
import java.io.InputStream;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

public class ServerService {

	private static HttpSolrServer server;

	private ServerService() {
	}

	public static void startServerWithIndex(String indexPath) {
		server = new HttpSolrServer(indexPath);
	}

	public static HttpSolrServer getServer() {
		return server;
	}

	public static <T> T searchQuery(MoreLikeThisQuery moreLikeThisQuery,
			ResponseConverter<T, InputStream> converter) throws Exception {
		HttpConnection con = new HttpConnection();
		String url = server.getBaseURL() + "/" + moreLikeThisQuery.getQueryString();
		System.out.println(url);
		InputStream data = con.sendPostNotSecure(url);

		// debugg
		// TestUtil.printinBuffer(data);

		return converter.parseResponse(data);
	}

	public static void deleteIndex() throws SolrServerException, IOException {
		server.deleteByQuery("*:*");
		server.commit();
	}

	public static <T> T executeQuery(SolrQuery query, ResponseConverter<T, QueryResponse> converter)
			throws Exception {
		QueryResponse response = server.query(query);
		return converter.parseResponse(response);
	}

}
