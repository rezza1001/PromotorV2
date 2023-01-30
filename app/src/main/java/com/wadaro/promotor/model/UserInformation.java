package com.wadaro.promotor.model;

/**
 * Created by pho0890910 on 2/20/2019.
 */
public class UserInformation {

    private String sessionId;
    private String username;
    private Integer userLevel;

    public UserInformation(String sessionId, String username, Integer userLevel) {
        this.sessionId = sessionId;
        this.username = username;
        this.userLevel = userLevel;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }
}
