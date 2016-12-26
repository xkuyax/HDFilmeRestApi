package me.xkuyax.hdfilme.rest;

import me.xkuyax.hdfilme.rest.api.HDFilmeTv.FilmSiteInfo;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv.SeriesSiteInfo;
import me.xkuyax.hdfilme.rest.api.series.SeriesInfo;
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
        VideoStreamDownloader videoStreamDownloader = new VideoStreamDownloader(serviceController.getDownloadHandler(), filmSiteInfo.getInfo().get(0).getUrl(), true);
        filmSiteInfo.getInfo().forEach(filmInfo -> System.out.println(filmInfo.getUrl()));
        List<VideoStreamLink> videoStreamLinkList = videoStreamDownloader.getLinks();
        System.out.println(videoStreamLinkList.get(0));
        serviceController.filmInfo("http://hdfilme.tv/hacked-kein-leben-ist-sicher-2016-3987-info");
    }

    @Test
    public void testSeries() throws Exception {
        ServiceController serviceController = new ServiceController();
        SeriesSiteInfo siteInfo = serviceController.getSeries(0);
        for (SeriesInfo seriesInfo : siteInfo.getInfo()) {
            System.out.println(seriesInfo.getCurrentEpisodes() + " " + seriesInfo.getMaxEpisodes());
        }
    }

    @Test
    public void thumbnail() throws Exception {
        String link = "http://hdfilme.tv/streetdance-new-york-2016-3999-stream";
        ServiceController serviceController = new ServiceController();
        serviceController.thumbnail(link, "21:9");
        serviceController.thumbnail(link, "4:3");
    }
}