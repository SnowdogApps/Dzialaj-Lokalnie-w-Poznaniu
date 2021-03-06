package pl.snowdog.dzialajlokalnie.model;


import com.activeandroid.annotation.Column;

public class NewUser {

    private String username;
    private String email;
    private String pass;
    private String description;
    private String name;
    private String surname;
    private Integer districtID;
    private Integer enableEmailNotifications;
    private Integer enablePushNotifications;
    private String pushRegId;
    private String avatarUri;


    public NewUser() {
        this.setDistrictID(null);
        this.setEnableEmailNotifications(null);
        this.setEnablePushNotifications(null);
    }

    public Integer getEnableEmailNotifications() {
        return enableEmailNotifications;
    }

    public void setEnableEmailNotifications(Integer enableEmailNotifications) {
        this.enableEmailNotifications = enableEmailNotifications;
    }

    public Integer getEnablePushNotifications() {
        return enablePushNotifications;
    }

    public void setEnablePushNotifications(Integer enablePushNotifications) {
        this.enablePushNotifications = enablePushNotifications;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getDistrictID() {
        return districtID;
    }

    public void setDistrictID(Integer districtID) {
        this.districtID = districtID;
    }

    public String getPushRegId() {
        return pushRegId;
    }

    public void setPushRegId(String pushRegId) {
        this.pushRegId = pushRegId;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }
}