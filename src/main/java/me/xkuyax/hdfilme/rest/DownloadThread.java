package me.xkuyax.hdfilme.rest;

import lombok.RequiredArgsConstructor;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv.SiteInfo;
import me.xkuyax.hdfilme.rest.api.film.FilmInfo;
import me.xkuyax.utils.Lambdas;

import java.util.stream.IntStream;

@RequiredArgsConstructor
public class DownloadThread extends Thread {

    private final ServiceController serviceController;

    @Override
    public void run() {
        try {
            downloadAll(serviceController::getFilms, videoInfo -> serviceController.videoUrl(videoInfo.getUrl(), true, -1));
            downloadAll(serviceController::getSeries, videoInfo -> {
                Lambdas.parallelForEachInt(40, IntStream.rangeClosed(1, videoInfo.getCurrentEpisodes()), i -> serviceController.videoUrl(videoInfo.getUrl(), true, i));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T extends FilmInfo> void downloadAll(SiteInfoSupplier<T> siteInfoSupplier, SiteInfoConsumer<T> videoInfoConsumer) throws Exception {
        SiteInfo filmSiteInfo = siteInfoSupplier.get(1);
        int max = filmSiteInfo.getMaxSite();
        Lambdas.parallelForEachInt(max, IntStream.rangeClosed(1, max), i -> {
            SiteInfo<T> videoSite = siteInfoSupplier.get(i);
            Lambdas.parallelForEach(32, videoSite.getInfo().stream(), videoInfoConsumer::accept);
        });
    }

    public interface SiteInfoConsumer<T extends FilmInfo> {

        void accept(T filmInfo) throws Exception;

    }

    public interface SiteInfoSupplier<T extends FilmInfo> {

        SiteInfo<T> get(int page) throws Exception;

    }
}
