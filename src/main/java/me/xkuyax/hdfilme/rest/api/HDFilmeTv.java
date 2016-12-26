package me.xkuyax.hdfilme.rest.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.xkuyax.hdfilme.rest.api.downloadapi.CacheDownloadHandler;
import me.xkuyax.hdfilme.rest.api.film.FilmInfo;
import me.xkuyax.hdfilme.rest.api.film.FilmSiteParser;
import me.xkuyax.hdfilme.rest.api.series.SeriesInfo;
import me.xkuyax.hdfilme.rest.api.series.SeriesSiteParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

@Data
public class HDFilmeTv {

    private final CacheDownloadHandler downloadHandler;
    private final String MOVIE_URL = "http://hdfilme.tv/movie-movies?order_f=id&order_d=desc&per_page=%s%";
    private final String SERIES_URL = "http://hdfilme.tv/movie-series?order_f=id&order_d=desc&per_page=%s%";

    public FilmSiteInfo downloadFilms(int site) throws IOException {
        DownloadPageInfo pageInfo = downloadSite(MOVIE_URL, "movies", site);
        return new FilmSiteInfo(pageInfo.getCurrentSite(), pageInfo.getMaxSite(), new FilmSiteParser(pageInfo.getDocument()).parse());
    }

    public SeriesSiteInfo downloadSeries(int site) throws IOException {
        DownloadPageInfo pageInfo = downloadSite(SERIES_URL, "series", site);
        return new SeriesSiteInfo(pageInfo.getCurrentSite(), pageInfo.getMaxSite(), new SeriesSiteParser(pageInfo.getDocument()).parse());
    }

    private DownloadPageInfo downloadSite(String baseUrl, String prefix, int site) throws IOException {
        String url = baseUrl.replaceAll("%s%", site + "");
        String html = downloadHandler.handleDownloadAsString(url, prefix + "/moviesite-" + site + ".html");
        Document document = Jsoup.parse(html);
        int currentSite = 0;
        int maxSite = 0;
        for (Element element : document.select(".fa.fa-caret-down")) {
            String siteInfo = element.parent().text();
            if (!siteInfo.isEmpty()) {
                String[] split = siteInfo.split("\\/");
                try {
                    String current = split[split.length - 2].replaceAll("Seite ", "");
                    currentSite = Integer.parseInt(current);
                    maxSite = Integer.parseInt(split[split.length - 1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new DownloadPageInfo(currentSite, maxSite, document);
    }

    @Data
    @AllArgsConstructor
    public static class SiteInfo<T> {

        private int currentSite;
        private int maxSite;
        private List<T> info;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FilmSiteInfo extends SiteInfo<FilmInfo> {

        public FilmSiteInfo(int currentSite, int maxSite, List<FilmInfo> filmInfo) {
            super(currentSite, maxSite, filmInfo);
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class SeriesSiteInfo extends SiteInfo<SeriesInfo> {

        public SeriesSiteInfo(int currentSite, int maxSite, List<SeriesInfo> seriesInfo) {
            super(currentSite, maxSite, seriesInfo);
        }
    }

    @Data
    @AllArgsConstructor
    private static class DownloadPageInfo {

        private int currentSite;
        private int maxSite;
        private Document document;

    }
}
