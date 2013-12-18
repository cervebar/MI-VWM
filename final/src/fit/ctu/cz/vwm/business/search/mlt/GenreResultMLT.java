package fit.ctu.cz.vwm.business.search.mlt;

public class GenreResultMLT {
	String idDoc;
	String genre;
	String score;
	String description;
	String filePath;
	String order;

	public void printInfo() {
		System.out.println("order: " + order + "idDoc: " + idDoc + ", genre: " + genre
				+ ", score: " + score + ", filePath:" + filePath + ", description: " + description);
	}

	public String getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(String idDoc) {
		this.idDoc = idDoc;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
