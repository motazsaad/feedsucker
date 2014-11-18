package rsssucker.article;

/**
 * Scraped web article data.
 */
public class ArticleData {

    private String text; // article text
    private String title; // article title

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
