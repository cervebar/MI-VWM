package fit.ctu.cz.vwm.business.search;

import java.io.InputStream;
import java.net.URLEncoder;

import fit.ctu.cz.vwm.business.search.mlt.MLTParams;
import fit.ctu.cz.vwm.dao.Constants;
import fit.ctu.cz.vwm.model.GenreResult;
import fit.ctu.cz.vwm.util.NotSecureConnection;

public class MLTSearch implements GenreSearch<MLTParams> {
	private final String serverCore;

	public MLTSearch(String serverCore) {
		this.serverCore = serverCore;
	}

	@Override
	public GenreResult getGenreResult(MLTParams params) throws Exception {
		NotSecureConnection http = new NotSecureConnection();

		String query = "&q=" + Constants.ID_PATH + ":";
		String fl = "&mlt.fl=description";
		String gf = "&mlt.gf=" + URLEncoder.encode("description^2", "UTF-8");

		// + params.idPath + params.category

		String queryMLT = "/mlt?mlt.match.include=false&mlt.boost=true" + fl + gf + query
				+ "&rows=10";

		String url = serverCore + queryMLT;

		InputStream data = http.sendPost(url);
		System.out.println(url);
		// NotSecureConnection.printinBuffer(data);
		return new GenreResult();
	}
}
