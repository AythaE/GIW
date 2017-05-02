package es.ugr.giw.p3.common;

/**
 * Class News used to store news of the EFE collection
 */
public class News {
	/** Title attribute */
	private final String title;
	/** Text attribute that store the news itself */
	private final String text;

	/**
	 * Constructor of News class, it takes a title and text (content) as parameters
	 * @param title the tittle of the news
	 * @param text the text of the news
	 */
	public News(String title, String text) {
		this.title = title;
		this.text = text;
	}

	/**
	 * Gets the title of the news
	 * @return the title of the news
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the text or content of the news
	 * @return the text of the news
	 */
	public String getText() {
		return text;
	}
	
	
}
