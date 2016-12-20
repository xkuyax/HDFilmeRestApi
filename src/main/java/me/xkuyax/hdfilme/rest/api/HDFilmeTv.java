package me.xkuyax.hdfilme.rest.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.xkuyax.hdfilme.rest.api.downloadapi.CacheDownloadHandler;
import me.xkuyax.hdfilme.rest.api.series.SeriesInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

@Data
public class HDFilmeTv {

    private final CacheDownloadHandler downloadHandler;
    private final String BASE_URL = "http://hdfilme.tv/movie-movies?order_f=id&order_d=desc&per_page=%s%";

    public FilmSiteInfo downloadFilms(int site) throws IOException {
        DownloadPageInfo pageInfo = downloadSite(BASE_URL, site);
        return new FilmSiteInfo(pageInfo.getCurrentSite(), pageInfo.getMaxSite(), new FilmSiteParser(pageInfo
                .getDocument()).parse());
    }

    public SeriesInfo downloadSeries(int site) throws IOException{
        DownloadPageInfo pageInfo = downloadSite(BASE_URL, site);
        return new FilmSiteInfo(pageInfo.getCurrentSite(), pageInfo.getMaxSite(), new er(pageInfo
                .getDocument()).parse());
    }

    private DownloadPageInfo downloadSite(String baseUrl, int site) throws IOException {
        String url = baseUrl.replaceAll("%s%", site + "");
        String html = downloadHandler.handleDownloadAsString(url, "moviesite-" + site + ".html");
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
    public static class FilmSiteInfo {

        private int currentSite;
        private int maxSite;
        private List<FilmInfo> filmInfo;

    }

    @Data
    @AllArgsConstructor
    public static class SeriesSiteInfo {

        private int currentSite;
        private int maxSite;
        private List<SeriesInfo> seriesInfos;

    }

    @Data
    @AllArgsConstructor
    private static class DownloadPageInfo {

        private int currentSite;
        private int maxSite;
        private Document document;

    }
}
