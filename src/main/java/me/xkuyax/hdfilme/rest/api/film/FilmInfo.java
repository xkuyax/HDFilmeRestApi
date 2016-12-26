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
    private String searchTitle;
    private String url;
    private String imageUrl;
    private String description;
    private List<String> genres;
    private int views;
    private int year;
    private float rating;
    private List<VideoDownloadLink> videoDownloadLinks;

    public FilmInfo(String title, String image, String description) {
        this.title = title;
        this.imageUrl = image;
        this.description = description;
    }

    public void setSearchTitle() {
        setSearchTitle(title.replaceAll("\\([0-9]{1,}\\)", ""));
    }
}
