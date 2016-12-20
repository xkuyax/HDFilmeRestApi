package me.xkuyax.hdfilme.rest.api.film;

import lombok.Data;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
public class FilmSiteParser {

    private final Document document;

    public List<FilmInfo> parse() {
        return document.select(".box-product.clearfix").stream().map(FilmSiteParser::parseFilmInfo).collect(Collectors.toList());
    }

    public static FilmInfo parseFilmInfo(Element filmInfo) {
        return parseFilmInfo(filmInfo, FilmInfo::new);
    }

    public static <T extends FilmInfo> T parseFilmInfo(Element infoElement, Supplier<T> supplier) {
        String url = infoElement.select("div.box-product.clearfix > a").attr("href");
        String img = infoElement.select("img").attr("src");
        String title = infoElement.select(".title-product").text();
        String desc = infoElement.select(".popover-content p").first().text();
        String fullText = infoElement.select(".popover-content").text();
        String metaData = fullText.substring(desc.length() + 1);
        String[] split = metaData.split("IMDB Punkt: ");
        List<String> genres = Arrays.stream(split[0].replaceAll("Genre: ", "").split(" ")).collect(Collectors.toList());
        float rating = Float.valueOf(split[1].replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
        int views = Integer.parseInt(infoElement.select("span.view-product").text().replaceAll("[^\\d.]", ""));
        url = url.replaceAll("info", "stream");
        T t = supplier.get();
        t.setTitle(title);
        t.setUrl(url);
        t.setImageUrl(img);
        t.setDescription(desc);
        t.setGenres(genres);
        t.setViews(views);
        t.setRating(rating);
        t.setVideoDownloadLinks(new ArrayList<>());
        return t;
    }
}
