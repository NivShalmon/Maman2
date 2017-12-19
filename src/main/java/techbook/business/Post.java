package techbook.business;



import java.sql.Timestamp;
import java.time.LocalDateTime;


public class Post {

    Integer id = -1;
    Integer author = -1;
    String text = null;
    Integer likes = 0;
    LocalDateTime date = null;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Timestamp getTimeStamp() {
        return date == null ? null : Timestamp.valueOf(date);
    }

    public void setDate(LocalDateTime LocalDateTime) {
        this.date = LocalDateTime;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.date = timeStamp.toLocalDateTime();
    }

    public LocalDateTime getDate()
    {
        return date;
    }

    public static Post badPost()
    {
        return new Post();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;

        Post post = (Post) o;

        if (getId() != null ? !getId().equals(post.getId()) : post.getId() != null) return false;
        if (getAuthor() != null ? !getAuthor().equals(post.getAuthor()) : post.getAuthor() != null) return false;
        if (getText() != null ? !getText().equals(post.getText()) : post.getText() != null) return false;
        if (getLikes() != null ? !getLikes().equals(post.getLikes()) : post.getLikes() != null) return false;
        return getDate() != null ? getDate().equals(post.getDate()) : post.getDate() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getAuthor() != null ? getAuthor().hashCode() : 0);
        result = 31 * result + (getText() != null ? getText().hashCode() : 0);
        result = 31 * result + (getLikes() != null ? getLikes().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Post{");
        sb.append("id=").append(id);
        sb.append(", author=").append(author);
        sb.append(", text='").append(text).append('\'');
        sb.append(", likes=").append(likes);
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
