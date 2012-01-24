package ch.ethz.origo.jerpa.data.tier.pojo;

import java.sql.Clob;
import java.sql.Date;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class Article {
    private int articleId;

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    private Clob text;

    public Clob getText() {
        return text;
    }

    public void setText(Clob text) {
        this.text = text;
    }

    private Date time;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (articleId != article.articleId) return false;
        if (version != article.version) return false;
        if (text != null ? !text.equals(article.text) : article.text != null) return false;
        if (time != null ? !time.equals(article.time) : article.time != null) return false;
        if (title != null ? !title.equals(article.title) : article.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = articleId;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private ResearchGroup researchGroup;

    public ResearchGroup getResearchGroup() {
        return researchGroup;
    }

    public void setResearchGroup(ResearchGroup researchGroup) {
        this.researchGroup = researchGroup;
    }

    private Collection<ArticlesComments> articleComments;

    public Collection<ArticlesComments> getArticleComments() {
        return articleComments;
    }

    public void setArticleComments(Collection<ArticlesComments> articleComments) {
        this.articleComments = articleComments;
    }

    private Collection<ArticlesSubscribtions> articleSubscribtions;

    public Collection<ArticlesSubscribtions> getArticleSubscribtions() {
        return articleSubscribtions;
    }

    public void setArticleSubscribtions(Collection<ArticlesSubscribtions> articleSubscribtions) {
        this.articleSubscribtions = articleSubscribtions;
    }
}
