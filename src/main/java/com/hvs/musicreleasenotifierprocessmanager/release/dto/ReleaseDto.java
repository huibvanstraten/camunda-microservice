package com.hvs.musicreleasenotifierprocessmanager.release.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleaseDto implements Serializable {
    private String name;
    private String artistId;

    public ReleaseDto() {
    }

    public ReleaseDto(String name, String artistId) {
        this.name = name;
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }
}