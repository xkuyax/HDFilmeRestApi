package me.xkuyax.hdfilme.rest.api.series;

import lombok.Getter;
import lombok.Setter;
import me.xkuyax.hdfilme.rest.api.FilmInfo;
import me.xkuyax.hdfilme.rest.api.download.VideoDownloadLink;

import java.util.List;

@Getter
@Setter
public class SeriesInfo extends FilmInfo {

    private final int episode;
    private final int maxEpisodes;

    public SeriesInfo(String title, String url, String imageUrl, String description, List<String> genres, int views, float rating, List<VideoDownloadLink> videoDownloadLinks, int episode, int maxEpisodes) {
        super(title, url, imageUrl, description, genres, views, rating, videoDownloadLinks);
        this.episode = episode;
        this.maxEpisodes = maxEpisodes;
    }
}
