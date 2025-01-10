package com.hvs.musicreleasenotifierprocessmanager.dto;

import java.io.Serializable;
import java.util.List;

public class UserDto implements Serializable {
    private String username;
    private List<String> artistIdList;

    public UserDto() {
    }

    public UserDto(String username, List<String> artistIdList) {
        this.username = username;
        this.artistIdList = artistIdList;
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getArtistIdList() {
        return artistIdList;
    }

    public void setArtistIdList(List<String> artistIdList) {
        this.artistIdList = artistIdList;
    }

    // toString() method (optional, for debugging)

    @Override
    public String toString() {
        return "UserDto{" +
                "username='" + username + '\'' +
                ", artistIdList=" + artistIdList +
                '}';
    }
}
