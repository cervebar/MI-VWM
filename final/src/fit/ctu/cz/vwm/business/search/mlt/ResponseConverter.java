package fit.ctu.cz.vwm.business.search.mlt;

public interface ResponseConverter<T, U> {

	T parseResponse(U data) throws Exception;

}
