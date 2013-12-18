package fit.ctu.cz.vwm.business.search.mlt;

import java.net.URLEncoder;
import java.util.List;

import fit.ctu.cz.vwm.dao.Constants;

public class MoreLikeThisExecutor {
	/**
	 * 
	 * 
	 * @param idDocument
	 * @param maxResults
	 * @param whatToCompare
	 *            - fileds in format filedNAme1,filedName2,filedName3
	 * @param filter
	 *            - filter query in standart Solr format ex.: group:\"HEAVY\" AND toRecommend:"true"
	 *            AND lang:\"en\"
	 * @return
	 * @throws Exception
	 *             - solr, encoding
	 */
	public static List<GenreResultMLT> executeMoreLikeThis(String indexPath, String idDocument,
			int maxResults, String whatToCompare, String filter) throws Exception {
		ServerService.startServerWithIndex(indexPath);

		MoreLikeThisQuery moreLikeThisQuery = new MoreLikeThisQuery(Constants.ID_USER_DEFINED
				+ ":\"" + idDocument + "\"");
		moreLikeThisQuery.setMaxResults(maxResults + "");
		moreLikeThisQuery.setFl("id" + "," + Constants.GENRE);
		moreLikeThisQuery.setMatchInclude(false);
		if (filter != null && !filter.equals("")) {
			moreLikeThisQuery.setFilterQuery(URLEncoder.encode(filter, "UTF-8"));
		}
		moreLikeThisQuery.setMltfl(whatToCompare);

		return ServerService.searchQuery(moreLikeThisQuery, new GengreResponseParser());

	}
}
