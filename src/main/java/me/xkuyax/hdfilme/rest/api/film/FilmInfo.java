package me.xkuyax.hdfilme.rest.api.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.xkuyax.hdfilme.rest.api.download.VideoDownloadLink;

import java.util.List;

@Data
@NoArgsConstructor
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
