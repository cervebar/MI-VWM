package fit.ctu.cz.vwm.business.search.mlt;

public class MoreLikeThisQuery {
	String maxRes;
	String filter;
	String matchInclude = "true";

	String minDocFreq = "1";
	String minTermFreq = "1";
	String minwl = "1";
	String query;
	String fl;
	String filterQuery;// fl

	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}

	String rows;

	public void setFl(String fl) {
		this.fl = fl;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public void setMaxqt(String maxqt) {
		this.maxqt = maxqt;
	}

	public void setMltfl(String mltfl) {
		this.mltfl = mltfl;
	}

	String boost = "false";
	String maxqt = "1000";
	String mltfl = "";
	private String maximumQueryTerm;

	public MoreLikeThisQuery(String query) {
		this.query = query;
	}

	public void setMaxResults(String maxRes2) {
		this.maxRes = maxRes2;
	}

	public void setMatchInclude(String matchInclude) {
		this.matchInclude = matchInclude;
	}

	public void setMatchInclude(boolean matchInclude) {
		this.matchInclude = matchInclude ? "true" : "false";
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setMaxRes(String maxRes) {
		this.maxRes = maxRes;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setMinDocFreq(String minDocFreq) {
		this.minDocFreq = minDocFreq;
	}

	public void setMinTermFreq(String minTermFreq) {
		this.minTermFreq = minTermFreq;
	}

	public void setMinwl(String minwl) {
		this.minwl = minwl;
	}

	public void setBoost(String boost) {
		this.boost = boost;
	}

	public void setBoost(boolean boost) {
		this.boost = boost ? "true" : "false";
	}

	public String getQueryString() {
		String resultQuery = "mlt?mlt.match.include=" + matchInclude + "&mlt.boost=" + boost
				+ "&q=" + query;
		if (filterQuery != null) {
			resultQuery += "&fq=" + filterQuery;
		}
		if (fl != null) {
			resultQuery += "&fl=" + fl;
		}
		if (rows != null) {
			resultQuery += "&rows=" + rows;
		}
		if (maximumQueryTerm != null) {
			resultQuery += "&mlt.maxqt=" + maximumQueryTerm;
		}
		if (minDocFreq != null) {
			resultQuery += "&mlt.mindf=" + minDocFreq;
		}
		if (minTermFreq != null) {
			resultQuery += "&mlt.mindf=" + minDocFreq;
		}
		if (minwl != null) {
			resultQuery += "&mlt.minwl=" + minwl;
		}
		if (mltfl != null) {
			resultQuery += "&mlt.fl=" + mltfl;
		}

		return resultQuery;

	}

	public String getQuery() {
		return query;
	}

}
