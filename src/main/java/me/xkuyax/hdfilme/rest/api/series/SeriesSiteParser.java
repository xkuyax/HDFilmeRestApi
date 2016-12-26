package me.xkuyax.hdfilme.rest.api.series;

import lombok.Data;
import me.xkuyax.hdfilme.rest.api.film.FilmSiteParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SeriesSiteParser {

    private final Document document;

    public List<SeriesInfo> parse() {
        return document.select(FilmSiteParser.CSS_ELEMENT_SELECTOR).stream().map(this::parseFilmInfo).collect(Collectors.toList());
    }

    public SeriesInfo parseFilmInfo(Element infoElement) {
        FilmSiteParser filmSiteParser = new FilmSiteParser(document);
        SeriesInfo seriesInfo = filmSiteParser.parseFilmInfo(infoElement, SeriesInfo::new);
        String episode = infoElement.select(".episode").text();
        seriesInfo.setMaxEpisodes(parseEpisode(episode, 1));
        seriesInfo.setCurrentEpisodes(parseEpisode(episode, 2));
        seriesInfo.setUrl(seriesInfo.getUrl() + "?episode=EPISODE");
        return seriesInfo;
    }

    private int parseEpisode(String episodeText, int index) {
        String[] split = episodeText.split("\\/");
        String text = split[split.length - (index >= split.length ? 1 : index)].trim();
        return text.isEmpty() ? -1 : Integer.parseInt(text);
    }
}
