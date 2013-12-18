package fit.ctu.cz.vwm.main;

import fit.ctu.cz.vwm.business.search.mlt.MoreLikeThisExecutor;
import fit.ctu.cz.vwm.dao.Constants;

public class TestMLT {

	public static void main(String[] args) throws Exception {
		testMLT(Constants.LOCALHOST);
	}

	private static void testMLT(String indexPath) throws Exception {
		int maxResults = 10;
		MoreLikeThisExecutor.executeMoreLikeThis(Constants.LOCALHOST, "udfID1", maxResults,
				Constants.DESCRIPTION, null);
	}
}
