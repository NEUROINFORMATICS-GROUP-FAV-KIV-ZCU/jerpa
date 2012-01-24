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
public class ArticlesComments {
    private int commentId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArticlesComments that = (ArticlesComments) o;

        if (commentId != that.commentId) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = commentId;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    private Article articles;

    public Article getArticles() {
        return articles;
    }

    public void setArticles(Article articles) {
        this.articles = articles;
    }

    private ArticlesComments articleComment;

    public ArticlesComments getArticleComment() {
        return articleComment;
    }

    public void setArticleComment(ArticlesComments articleComment) {
        this.articleComment = articleComment;
    }

    private Collection<ArticlesComments> articleComments;

    public Collection<ArticlesComments> getArticleComments() {
        return articleComments;
    }

    public void setArticleComments(Collection<ArticlesComments> articleComments) {
        this.articleComments = articleComments;
    }

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
