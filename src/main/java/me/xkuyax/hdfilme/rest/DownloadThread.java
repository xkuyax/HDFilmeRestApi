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
            downloadAll((page) -> {
                try {
                    return serviceController.getFilms(page);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            downloadAll((page) -> {
                try {
                    return serviceController.getSeries(page);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadAll(SiteInfoSupplier siteInfoSupplier) throws IOException {
        SiteInfo filmSiteInfo = siteInfoSupplier.get(0);//serviceController.getMovieListDownloader().downloadFilms(0);
        int max = filmSiteInfo.getMaxSite();
        for (int i = 0; i <= max; i++) {
            SiteInfo<? extends FilmInfo> videoSite = siteInfoSupplier.get(i * 50);//serviceController.getMovieListDownloader().downloadFilms(i * 50);
            ForkJoinPool forkJoinPool = new ForkJoinPool(32);
            forkJoinPool.submit(() -> {
                videoSite.getInfo().parallelStream().forEach(videoInfo -> {
                    try {
                        serviceController.videoUrl(videoInfo.getUrl(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    public interface SiteInfoSupplier {

        SiteInfo<? extends FilmInfo> get(int page);

    }
}
