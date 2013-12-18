package fit.ctu.cz.vwm.main;

import fit.ctu.cz.vwm.business.search.GenreSearch;
import fit.ctu.cz.vwm.business.search.MLTSearch;
import fit.ctu.cz.vwm.business.search.mlt.MLTParams;
import fit.ctu.cz.vwm.dao.Constants;
import fit.ctu.cz.vwm.model.GenreResult;

public class SearchTest {

	public static void main(String[] args) throws Exception {
		searchByMLT();
	}

	private static void searchByMLT() throws Exception {
		// 1. extract

		// 2. save as CATEGORY_NO_GENRE_EXTRACTED

		// 3. get id_path to saved document
		String idPath = "dataset/punk/VisaciZamekZatykac.mp3";
		// 4. execute more like this search
		GenreSearch<MLTParams> searchInterface = new MLTSearch(Constants.LOCALHOST);
		MLTParams params = new MLTParams(idPath);
		params.category = "+AND+" + Constants.category + ":" + Constants.CATEGORY_GROUND_TRUTH;
		GenreResult result = searchInterface.getGenreResult(params);
		result.pritnInfo();
	}
}
