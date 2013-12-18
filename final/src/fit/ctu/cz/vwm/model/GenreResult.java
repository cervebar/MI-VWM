package fit.ctu.cz.vwm.model;

import java.util.ArrayList;
import java.util.List;

public class GenreResult {
	String bestGenre;
	List<GenreStats> genres = new ArrayList<>();

	public void pritnInfo() {
		System.out.println("Best genre: " + bestGenre);
		System.out.println("Other: ");
		for (int i = 0; i < genres.size(); i++) {
			GenreStats g = genres.get(i);
			System.out.println(i + " - name: " + g.genreName + ", relevance: " + g.relevance);
		}
	}
}
