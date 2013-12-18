package fit.ctu.cz.vwm.business.search;

import fit.ctu.cz.vwm.model.GenreResult;

public interface GenreSearch<T> {

	GenreResult getGenreResult(T toSearch) throws Exception;

}
