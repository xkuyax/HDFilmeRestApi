package me.xkuyax.hdfilme.rest;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.xkuyax.hdfilme.rest.api.FileUtils;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv.FilmSiteInfo;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv.SeriesSiteInfo;
import me.xkuyax.hdfilme.rest.api.Login;
import me.xkuyax.hdfilme.rest.api.QualityLevel;
import me.xkuyax.hdfilme.rest.api.downloadapi.BaseFileSupplier;
import me.xkuyax.hdfilme.rest.api.downloadapi.CacheDownloadHandler;
import me.xkuyax.hdfilme.rest.api.film.FilmInfo;
import me.xkuyax.hdfilme.rest.api.film.FilmInfoParser;
import me.xkuyax.hdfilme.rest.api.search.SearchResults;
import me.xkuyax.hdfilme.rest.api.stream.VideoStreamDownloader;
import me.xkuyax.hdfilme.rest.api.stream.VideoStreamLink;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@RestController
@Getter
public class ServiceController {

    private final CacheDownloadHandler downloadHandler;
    private final HDFilmeTv movieListDownloader;
    private final TmdbApi tmdbApi = new TmdbApi("dd1ff349c915be67893eb5202f2e4aa9");

    public ServiceController() throws Exception {
        Login login = new Login("http://hdfilme.tv", "hdfilme");
        CloseableHttpClient httpClient = login.run();
        BaseFileSupplier baseFileSupplier = () -> Paths.get("data");
        downloadHandler = new CacheDownloadHandler(httpClient, baseFileSupplier);
        movieListDownloader = new HDFilmeTv(downloadHandler);
        //new DownloadThread(this).start();
    }

    @RequestMapping("/overview")
    public List<MenuType> getMainMenu() {
        return Collections.singletonList(new MenuType("films", "Filme"));
    }

    @RequestMapping("/films")
    public FilmSiteInfo getFilms(@RequestParam(defaultValue = "1") int page) throws IOException {
        System.out.println(page);
        FilmSiteInfo filmSiteInfo = movieListDownloader.downloadFilms(page);
        return filmSiteInfo;
    }

    @RequestMapping("/series")
    public SeriesSiteInfo getSeries(@RequestParam(defaultValue = "1") int page) throws IOException {
        System.out.println("Series " + page);
        return movieListDownloader.downloadSeries(page);
    }

    @RequestMapping("/videoUrl")
    public VideoStreamLink videoUrl(@RequestParam String link, @RequestParam(defaultValue = "true") boolean cache, @RequestParam(defaultValue = "-1") int episode) throws IOException {
        if (episode != -1) {
            link = link.replaceAll("EPISODE", episode + "");
        }
        VideoStreamDownloader videoStreamDownloader = new VideoStreamDownloader(downloadHandler, link, cache);
        List<VideoStreamLink> links = videoStreamDownloader.getLinks();
        if (links.size() < 1) {
            //System.out.println("konnte keine links finden für " + link);
            return new VideoStreamLink("null", "null", "null", QualityLevel.BULLSHIT);
        } else {
            return links.get(0);
        }
    }

    @RequestMapping("/filmInfo")
    public FilmInfo filmInfo(@RequestParam String link) throws IOException {
        String html = downloadHandler.handleDownloadAsString(link.replaceAll("-stream", "-info"), "info/" + FileUtils.removeInvalidFileNameChars(link));
        Document document = Jsoup.parse(html);
        FilmInfoParser infoParser = new FilmInfoParser(document);
        FilmInfo filmInfo = infoParser.parse();
        filmInfo.setUrl(link);
        return filmInfo;
    }

    @RequestMapping(value = "/thumbnail", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] thumbnail(@RequestParam String link, @RequestParam(defaultValue = "21:9") String format) throws IOException {
        // System.out.println("thumbnail");
        FilmInfo filmInfo = filmInfo(link);
        MovieResultsPage resultsPage = tmdbApi.getSearch().searchMovie(filmInfo.getSearchTitle(), filmInfo.getYear(), "de-DE", false, 0);
        if (resultsPage.getResults().size() > 0) {
            //System.out.println("found some shit "+link);
            MovieDb movieDb = resultsPage.getResults().get(0);

            String url = "https://image.tmdb.org/t/p/w500/" + (format.equals("21:9") ? movieDb.getBackdropPath() : movieDb.getPosterPath());

            return downloadHandler.handleDownload(url, "moviedb/" + FileUtils.removeInvalidFileNameChars(url));
        } else {
            //System.out.println("found no shit "+link);
        }
        return downloadHandler.handleDownload(filmInfo.getImageUrl(), "images/" + FileUtils.removeInvalidFileNameChars(link));
    }

    @RequestMapping(value = "/search")
    public SearchResults search(
            @RequestParam
                    String query) {

        return movieListDownloader.downloadSearch(query);
    }

    @AllArgsConstructor
    public static class MenuType {

        private String id;
        private String title;

    }
}
