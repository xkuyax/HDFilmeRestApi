package me.xkuyax.hdfilme.rest.api.film;

import lombok.Data;
import me.xkuyax.hdfilme.rest.api.RegexUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
public class FilmSiteParser {

    public static final String CSS_ELEMENT_SELECTOR = ".products .box-product.clearfix";
    private final Document document;

    public List<FilmInfo> parse() {
        return document.select(CSS_ELEMENT_SELECTOR).stream().map(this::parseFilmInfo).collect(Collectors.toList());
    }

    public FilmInfo parseFilmInfo(Element filmInfo) {
        return parseFilmInfo(filmInfo, FilmInfo::new);
    }

    public <T extends FilmInfo> T parseFilmInfo(Element infoElement, Supplier<T> supplier) {
        Element popOverElement = document.getElementById(infoElement.attr("data-popover"));
        String url = infoElement.select("div.box-product.clearfix > a").attr("href");
        String img = infoElement.select("img").attr("src");
        String title = infoElement.select(".title-product").text();
        String desc = popOverElement.select(".popover-content p").first().text();
        String fullText = popOverElement.select(".popover-content").text();
        String metaData = fullText.substring(desc.length() + 1).replaceAll("Bewerten: Favorisieren Später anschauen", "");
        String[] split = metaData.split("IMDb: ");
        String year = RegexUtils.extractLast(title, ".*[0-9]{4,}.*");
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
        t.setSearchTitle();
        if (!year.isEmpty()) {
            try {
                t.setYear(Integer.parseInt(year));
            } catch (Exception e) {
            }
        }
        return t;
    }
}
