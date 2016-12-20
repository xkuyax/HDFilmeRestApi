package me.xkuyax.hdfilme.rest.api.series;

import lombok.Data;
import me.xkuyax.hdfilme.rest.api.FilmInfo;
import me.xkuyax.hdfilme.rest.api.FilmSiteParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SeriesSiteParser {

    private final Document document;

    public List<FilmInfo> parse() {
        return document.select(".box-product.clearfix").stream().map(FilmSiteParser::parseFilmInfo).collect(Collectors.toList());
    }

    public static FilmInfo parseFilmInfo(Element filmInfo) {
        String url = filmInfo.select("div.box-product.clearfix > a").attr("href");
        String img = filmInfo.select("img").attr("src");
        String title = filmInfo.select(".title-product").text();
        String desc = filmInfo.select(".popover-content p").first().text();
        String fullText = filmInfo.select(".popover-content").text();
        String metaData = fullText.substring(desc.length() + 1);
        String[] split = metaData.split("IMDB Punkt: ");
        List<String> genres = Arrays.stream(split[0].replaceAll("Genre: ", "").split(" ")).collect(Collectors.toList());
        float rating = Float.valueOf(split[1].replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
        int views = Integer.parseInt(filmInfo.select("span.view-product").text().replaceAll("[^\\d.]", ""));
        url = url.replaceAll("info", "stream");
        return new FilmInfo(title, url, img, desc, genres, views, rating, new ArrayList<>());
    }
}
