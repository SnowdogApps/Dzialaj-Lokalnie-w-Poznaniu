package pl.snowdog.dzialajlokalnie.model;

import com.activeandroid.Model;

import java.util.ArrayList;
import java.util.Arrays;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import android.support.annotation.NonNull;
import android.util.Log;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import pl.snowdog.dzialajlokalnie.AddEventActivity_;
import pl.snowdog.dzialajlokalnie.util.ListHelper;


@Table(name = "Issues")
public class Issue extends Model implements Serializable {

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int issueID;

    @Column
    private int parentID;

    @Column
    private int userID;

    @Column
    private String authorName;

    @Column
    private String authorAvatar;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private int districtID;

    @Column
    private double lon;

    @Column
    private double lat;

    @Column
    private String address;

    @Column
    private String facebookUrl;

    @Column
    private String issueUrl;

    @Column
    private Date createdAt;

    @Column
    private int issueStatus;

    @Column
    private String categoryID;

    @Column
    private String photoIssueUri;

    @Column
    private int issueRating;

    @Column
    private int commentsCount;

    @Column
    private int votesCount;

    @Column
    private int userVotedValue;

    @Column
    private List<Integer> categoriesList;

    //TODO for offline usage these fields could be stored in db
    private String categoriesText;
    private String districtName;

    public Issue() {
        super();
    }

    public Issue(int issueID, int parentID, int userID, String authorName, String title, String description, int districtID, double lon, double lat, String address, String facebookUrl, String issueUrl, Date createdAt, int issueStatus, String categoryID, String photoIssueUri, int issueRating, int commentsCount, int votesCount, int userVotedValue, List<Integer> categoriesList, String categoriesText) {
        super();

        this.issueID = issueID;
        this.parentID = parentID;
        this.userID = userID;
        this.authorName = authorName;
        this.title = title;
        this.description = description;
        this.districtID = districtID;
        this.lon = lon;
        this.lat = lat;
        this.address = address;
        this.facebookUrl = facebookUrl;
        this.issueUrl = issueUrl;
        this.createdAt = createdAt;
        this.issueStatus = issueStatus;
        this.categoryID = categoryID;
        this.photoIssueUri = photoIssueUri;
        this.issueRating = issueRating;
        this.commentsCount = commentsCount;
        this.votesCount = votesCount;
        this.userVotedValue = userVotedValue;
        this.categoriesList = categoriesList;
        this.categoriesText = categoriesText;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "issueID=" + issueID +
                ", parentID=" + parentID +
                ", userID=" + userID +
                ", authorName='" + authorName + '\'' +
                ", authorAvatar='" + authorAvatar + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", districtID=" + districtID +
                ", lon=" + lon +
                ", lat=" + lat +
                ", address='" + address + '\'' +
                ", facebookUrl='" + facebookUrl + '\'' +
                ", issueUrl='" + issueUrl + '\'' +
                ", createdAt=" + createdAt +
                ", issueStatus=" + issueStatus +
                ", categoryID='" + categoryID + '\'' +
                ", photoIssueUri='" + photoIssueUri + '\'' +
                ", issueRating=" + issueRating +
                ", commentsCount=" + commentsCount +
                ", votesCount=" + votesCount +
                ", userVotedValue=" + userVotedValue +
                ", categoriesList=" + categoriesList +
                ", categoriesText='" + categoriesText + '\'' +
                ", districtName='" + districtName + '\'' +
                '}';
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getUserVotedValue() {
        return userVotedValue;
    }

    public List<Integer> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<Integer> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getCategoriesText() {
        return categoriesText;
    }

    public void setCategoriesText(String categoriesText) {
        this.categoriesText = categoriesText;
    }

    public int isUserVotedValue() {
        return userVotedValue;
    }

    public void setUserVotedValue(int userVotedValue) {
        this.userVotedValue = userVotedValue;
    }

    public int getIssueID() {
        return issueID;
    }

    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDistrictID() {
        return districtID;
    }

    public void setDistrictID(int districtID) {
        this.districtID = districtID;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getIssueUrl() {
        return issueUrl;
    }

    public void setIssueUrl(String issueUrl) {
        this.issueUrl = issueUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(int issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getPhotoIssueUri() {
        return photoIssueUri;
    }

    public void setPhotoIssueUri(String photoIssueUri) {
        this.photoIssueUri = photoIssueUri;
    }

    public int getIssueRating() {
        return issueRating;
    }

    public void setIssueRating(int issueRating) {
        this.issueRating = issueRating;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    public void parseCategoriesList() {
        categoriesList = ListHelper.parseCategoryListFromString(categoryID);
    }

    public List<Integer> getCategoryIdsList() {
        return categoriesList;
    }


    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictName() {
        return districtName;
    }


}