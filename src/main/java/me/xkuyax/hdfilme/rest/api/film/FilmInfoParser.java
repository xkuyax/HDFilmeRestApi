package me.xkuyax.hdfilme.rest.api.film;

import lombok.Data;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

@Data
public class FilmInfoParser {

    private final Document document;

    public FilmInfo parse() {
        Element infoElement = document.select(".decapdion.clearfix").first();
        if (infoElement != null) {
            String image = infoElement.select(".img-thumb img").attr("src");
            String title = infoElement.select(".text-blue.title-film").text();
            String description = infoElement.select(".caption").text();
            FilmInfo filmInfo = new FilmInfo(title, image, description);
            filmInfo.setSearchTitle();
            Map<String, String> keyValueData = getKeyValue(infoElement.select(".movie-info-scroll p"));
            if (keyValueData.containsKey("Erscheinungsjahr")) {
                filmInfo.setYear(Integer.parseInt(keyValueData.get("Erscheinungsjahr")));
            }
            return filmInfo;
        }
        return null;
    }

    private Map<String, String> getKeyValue(Elements keyValueData) {
        Map<String, String> keys = new HashMap<>();
        for (Element element : keyValueData) {
            String text = element.text();
            String[] split = text.split(":");
            if (split.length > 1) {
                String key = split[0].trim();
                String value = split[1].trim();
                keys.put(key, value);
            }
        }
        return keys;
    }
}
