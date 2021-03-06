package pl.snowdog.dzialajlokalnie.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


@Table(name = "Comments")
public class Comment extends Model{


    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int commentID;

    @Column
    private int userID;

    @Column
    private int parentID;

    @Column
    private int parentType;

    @Column
    private int solution;

    @Column
    private String text;

    @Column
    private Date createdAt;

    @Column
    private Date updatedAt;

    @Column
    private String commentHashtags;

    @Column
    private String commentMentioned;

    @Column
    private int commentRating;

    @Column
    private int authorID;

    @Column
    private String authorName;

    @Column
    private String authorAvatar;

    @Column
    private int userVotedValue;


    public Comment() {
        super();
    }

    public Comment(int commentID, int userID, int parentID, int parentType, int solution, String text, Date createdAt, Date updatedAt, String commentHashtags, String commentMentioned, int commentRating, int authorID, String authorName, String authorAvatar, int userVotedValue) {
        super();

        this.commentID = commentID;
        this.userID = userID;
        this.parentID = parentID;
        this.parentType = parentType;
        this.solution = solution;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentHashtags = commentHashtags;
        this.commentMentioned = commentMentioned;
        this.commentRating = commentRating;
        this.authorID = authorID;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.userVotedValue = userVotedValue;
    }

    public Comment(int intParentType, int parentID, int solution, String text) {
        super();
        this.parentType = intParentType;
        this.parentID = parentID;
        this.solution = solution;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentID=" + commentID +
                ", userID=" + userID +
                ", parentID=" + parentID +
                ", parentType=" + parentType +
                ", solution=" + solution +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", commentHashtags='" + commentHashtags + '\'' +
                ", commentMentioned='" + commentMentioned + '\'' +
                ", commentRating=" + commentRating +
                ", authorID=" + authorID +
                ", authorName='" + authorName + '\'' +
                ", authorAvatar='" + authorAvatar + '\'' +
                ", userVotedValue=" + userVotedValue +
                '}';
    }


    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getCommentRating() {
        return commentRating;
    }

    public void setCommentRating(int commentRating) {
        this.commentRating = commentRating;
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public int getParentType() {
        return parentType;
    }

    public void setParentType(int parentType) {
        this.parentType = parentType;
    }

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCommentHashtags() {
        return commentHashtags;
    }

    public void setCommentHashtags(String commentHashtags) {
        this.commentHashtags = commentHashtags;
    }

    public String getCommentMentioned() {
        return commentMentioned;
    }

    public void setCommentMentioned(String commentMentioned) {
        this.commentMentioned = commentMentioned;
    }

    public void setUserVotedValue(int userVotedValue) {
        this.userVotedValue = userVotedValue;
    }

    public int getUserVotedValue() {
        return userVotedValue;
    }
}