package com.hvs.musicreleasenotifierprocessmanager.release.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class ReleaseDto implements Serializable {
    private String releaseId;
    private String artistId;
    private Integer totalTracks;
    private String name;
    private LocalDate releaseDate;
    private String type;
    private String spotifyUrl;

    public ReleaseDto() {
    }

    public ReleaseDto(String releaseId, String artistId, Integer totalTracks, String name, LocalDate releaseDate, String type, String spotifyUrl) {
        this.releaseId = releaseId;
        this.artistId = artistId;
        this.totalTracks = totalTracks;
        this.name = name;
        this.releaseDate = releaseDate;
        this.type = type;
        this.spotifyUrl = spotifyUrl;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public Integer getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(Integer totalTracks) {
        this.totalTracks = totalTracks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReleaseDto that)) return false;
        return Objects.equals(releaseId, that.releaseId) &&
                Objects.equals(artistId, that.artistId) &&
                Objects.equals(totalTracks, that.totalTracks) &&
                Objects.equals(name, that.name) &&
                Objects.equals(releaseDate, that.releaseDate) &&
                Objects.equals(type, that.type) &&
                Objects.equals(spotifyUrl, that.spotifyUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(releaseId, artistId, totalTracks, name, releaseDate, type, spotifyUrl);
    }

    @Override
    public String toString() {
        return "ReleaseDto{" +
                "releaseId='" + releaseId + '\'' +
                ", artistId='" + artistId + '\'' +
                ", totalTracks=" + totalTracks +
                ", name='" + name + '\'' +
                ", releaseDate=" + releaseDate +
                ", type='" + type + '\'' +
                ", spotifyUrl='" + spotifyUrl + '\'' +
                '}';
    }
}

