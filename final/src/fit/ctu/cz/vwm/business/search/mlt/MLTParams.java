package fit.ctu.cz.vwm.business.search.mlt;

import java.util.Map;

public class MLTParams {
	public String idPath;
	public Map<String, String> paramMap;
	public String category = "";// by default no cathegory search

	public MLTParams(String idPath) {
		this.idPath = idPath;
	}

}
