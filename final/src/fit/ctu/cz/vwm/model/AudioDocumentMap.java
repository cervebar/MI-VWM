package fit.ctu.cz.vwm.model;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class AudioDocumentMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 530926059164203468L;

	public void printInfo() {
		printInfo(System.out);

	}

	public void printInfo(PrintStream out) {
		Set<Entry<String, Object>> set = this.entrySet();
		for (Entry<String, Object> entry : set) {
			out.println(entry.getKey() + "-" + entry.getValue());
		}
	}

}
