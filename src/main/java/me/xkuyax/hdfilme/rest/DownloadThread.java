package me.xkuyax.hdfilme.rest;

import lombok.RequiredArgsConstructor;
import me.xkuyax.hdfilme.rest.api.HDFilmeTv.FilmSiteInfo;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
public class DownloadThread extends Thread {

    private final ServiceController serviceController;

    @Override
    public void run() {
        try {
            FilmSiteInfo filmSiteInfo = serviceController.getMovieListDownloader().downloadFilms(0);
            int max = filmSiteInfo.getMaxSite();
            for (int i = 0; i <= max; i++) {
                FilmSiteInfo filmSite = serviceController.getMovieListDownloader().downloadFilms(i * 50);
                ForkJoinPool forkJoinPool = new ForkJoinPool(32);
                forkJoinPool.submit(() -> {
                    filmSite.getFilmInfo().parallelStream().forEach(filmInfo -> {
                        try {
                            serviceController.videoUrl(filmInfo.getUrl(), true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
