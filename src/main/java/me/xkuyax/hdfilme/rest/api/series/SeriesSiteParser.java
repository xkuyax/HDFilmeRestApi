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
        return document.select(".box-product.clearfix").stream().map(SeriesSiteParser::parseFilmInfo).collect(Collectors.toList());
    }

    public static SeriesInfo parseFilmInfo(Element infoElement) {
        SeriesInfo seriesInfo = FilmSiteParser.parseFilmInfo(infoElement, SeriesInfo::new);
        return seriesInfo;
    }
}
