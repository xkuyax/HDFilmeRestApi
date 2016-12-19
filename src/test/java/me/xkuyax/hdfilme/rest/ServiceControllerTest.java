package me.xkuyax.hdfilme.rest;

import me.xkuyax.hdfilme.rest.api.HDFilmeTv.FilmSiteInfo;
import me.xkuyax.hdfilme.rest.api.stream.VideoStreamDownloader;
import me.xkuyax.hdfilme.rest.api.stream.VideoStreamLink;
import org.junit.Test;

import java.util.List;

public class ServiceControllerTest {

    @Test
    public void hi() throws Exception {
        ServiceController serviceController = new ServiceController();
        System.out.println(serviceController.getMainMenu());
        FilmSiteInfo filmSiteInfo = serviceController.getFilms(1);
        System.out.println(filmSiteInfo.getCurrentSite());
        VideoStreamDownloader videoStreamDownloader = new VideoStreamDownloader(serviceController.getDownloadHandler(), filmSiteInfo.getFilmInfo().get(0).getUrl());
        filmSiteInfo.getFilmInfo().forEach(filmInfo -> System.out.println(filmInfo.getUrl()));
        List<VideoStreamLink> videoStreamLinkList = videoStreamDownloader.getLinks();
        System.out.println(videoStreamLinkList.get(0));
    }
}