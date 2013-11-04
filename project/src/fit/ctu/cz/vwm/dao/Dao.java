package fit.ctu.cz.vwm.dao;

public interface Dao<T> {

	T getDocument(String id) throws Exception;

	void save(T doc) throws Exception;

}
