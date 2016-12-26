package me.xkuyax.hdfilme.rest.api.film;

import me.xkuyax.hdfilme.rest.api.series.SeriesSiteParser;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FilmSiteParserTest {

    @Test
    public void parse() throws Exception {
        String html = new String(Files.readAllBytes(Paths.get("data/series/moviesite-1.html")));
        SeriesSiteParser filmSiteParser = new SeriesSiteParser(Jsoup.parse(html));
        filmSiteParser.parse();
    }
}