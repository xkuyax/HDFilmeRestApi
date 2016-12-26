package me.xkuyax.hdfilme.rest;

import lombok.RequiredArgsConstructor;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv.SiteInfo;
import me.xkuyax.hdfilme.rest.api.film.FilmInfo;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
public class DownloadThread extends Thread {

    private final ServiceController serviceController;

    @Override
    public void run() {
        try {
            downloadAll(serviceController::getSeries, videoInfo -> {
                for (int i = 1; i <= videoInfo.getCurrentEpisodes(); i++) {
                    serviceController.videoUrl(videoInfo.getUrl(), true, i);
                }
            });
            downloadAll(serviceController::getFilms, videoInfo -> serviceController.videoUrl(videoInfo.getUrl(), true, -1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T extends FilmInfo> void downloadAll(SiteInfoSupplier<T> siteInfoSupplier, SiteInfoConsumer<T> videoInfoConsumer) throws IOException {
        SiteInfo filmSiteInfo = siteInfoSupplier.get(0);//serviceController.getMovieListDownloader().downloadFilms(0);
        int max = filmSiteInfo.getMaxSite();
        for (int i = 0; i < max; i++) {
            SiteInfo<T> videoSite = siteInfoSupplier.get(i * 50);//serviceController.getMovieListDownloader().downloadFilms(i * 50);
            ForkJoinPool forkJoinPool = new ForkJoinPool(32);
            forkJoinPool.submit(() -> {
                videoSite.getInfo().parallelStream().forEach(videoInfo -> {
                    try {
                        videoInfoConsumer.accept(videoInfo);
                        //
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    public interface SiteInfoConsumer<T extends FilmInfo> {

        void accept(T filmInfo) throws IOException;

    }

    public interface SiteInfoSupplier<T extends FilmInfo> {

        SiteInfo<T> get(int page) throws IOException;

    }
}
