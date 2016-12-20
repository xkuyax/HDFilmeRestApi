package me.xkuyax.hdfilme.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.xkuyax.hdfilme.rest.api.FileUtils;
import me.xkuyax.hdfilme.rest.api.FilmInfo;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv.FilmSiteInfo;
import me.xkuyax.hdfilme.rest.api.Login;
import me.xkuyax.hdfilme.rest.api.downloadapi.BaseFileSupplier;
import me.xkuyax.hdfilme.rest.api.downloadapi.CacheDownloadHandler;
import me.xkuyax.hdfilme.rest.api.info.FilmInfoParser;
import me.xkuyax.hdfilme.rest.api.stream.VideoStreamDownloader;
import me.xkuyax.hdfilme.rest.api.stream.VideoStreamLink;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    public ServiceController() throws Exception {
        Login login = new Login("http://hdfilme.tv", "hdfilme");
        CloseableHttpClient httpClient = login.run();
        BaseFileSupplier baseFileSupplier = () -> Paths.get("data");
        downloadHandler = new CacheDownloadHandler(httpClient, baseFileSupplier);
        movieListDownloader = new HDFilmeTv(downloadHandler);
        new DownloadThread(this).start();
    }

    @RequestMapping("/overview")
    public List<MenuType> getMainMenu() {
        System.out.println("main menu");
        return Collections.singletonList(new MenuType("films", "Filme"));
    }

    @RequestMapping("/films")
    public FilmSiteInfo getFilms(@RequestParam(defaultValue = "1") int page) throws IOException {
        System.out.println("Got page " + page);
        FilmSiteInfo filmSiteInfo = movieListDownloader.downloadFilms((page - 1) * 50);
        return filmSiteInfo;
    }



    @RequestMapping("/videoUrl")
    public VideoStreamLink videoUrl(@RequestParam String link, @RequestParam(defaultValue = "true") boolean cache) throws IOException {
        VideoStreamDownloader videoStreamDownloader = new VideoStreamDownloader(downloadHandler, link, cache);
        List<VideoStreamLink> links = videoStreamDownloader.getLinks();
        return links.get(0);
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

    @RequestMapping("/thumbnail")
    public byte[] thumbnail(@RequestParam String link) throws IOException {
        System.out.println(link);
        FilmInfo filmInfo = filmInfo(link);
        return downloadHandler.handleDownload(filmInfo.getImageUrl(), "images/" + FileUtils.removeInvalidFileNameChars(link));
    }

    @Data
    @AllArgsConstructor
    public static class VideoInfo {

        private String url;
        private String title;
        private String summary;
        private String thumbnail;

    }

    @Data
    @AllArgsConstructor
    public static class MenuType {

        private String id;
        private String title;

    }
}
