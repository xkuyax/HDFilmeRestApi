package me.xkuyax.hdfilme.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.xkuyax.hdfilme.rest.api.FilmInfo;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv.FilmSiteInfo;
import me.xkuyax.hdfilme.rest.api.Login;
import me.xkuyax.hdfilme.rest.api.downloadapi.BaseFileSupplier;
import me.xkuyax.hdfilme.rest.api.downloadapi.CacheDownloadHandler;
import me.xkuyax.hdfilme.rest.api.stream.VideoStreamDownloader;
import me.xkuyax.hdfilme.rest.api.stream.VideoStreamLink;
import org.apache.http.impl.client.CloseableHttpClient;
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
    }

    @RequestMapping("/overview")
    public List<MenuType> getMainMenu() {
        System.out.println("main menu");
        return Collections.singletonList(new MenuType("films", "Filme"));
    }

    @RequestMapping("/films")
    public FilmSiteInfo getFilms(@RequestParam(defaultValue = "1") int page) throws IOException {
        System.out.println("Got page " + page);
        FilmSiteInfo filmSiteInfo = movieListDownloader.downloadSite((page - 1) * 50);
        for (FilmInfo filmInfo : filmSiteInfo.getFilmInfo()) {
            VideoStreamDownloader videoStreamDownloader = new VideoStreamDownloader(downloadHandler, filmInfo.getUrl());
            List<VideoStreamLink> links = videoStreamDownloader.getLinks();
            filmInfo.setUrl(links.get(0).getFile());
        }
        return filmSiteInfo;
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
