package me.xkuyax.hdfilme.rest.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.xkuyax.hdfilme.rest.api.download.VideoDownloadLink;

import java.util.List;

@Data
@AllArgsConstructor
public class FilmInfo {

    private String title;
    private String url;
    private String imageUrl;
    private String description;
    private List<String> genres;
    private int views;
    private float rating;
    private List<VideoDownloadLink> videoDownloadLinks;

}
