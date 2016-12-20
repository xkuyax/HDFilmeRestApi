package me.xkuyax.hdfilme.rest.api.info;

import lombok.Data;
import me.xkuyax.hdfilme.rest.api.FilmInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

@Data
public class FilmInfoParser {

    private final Document document;

    public FilmInfo parse() {
        Element infoElement = document.select(".decapdion.clearfix").first();
        if (infoElement != null) {
            String image = infoElement.select(".img-thumb img").attr("src");
            String title = infoElement.select(".text-blue.title-film").text();
            String description = infoElement.select(".caption").text();
            return new FilmInfo(title, null, image, description, new ArrayList<>(), 0, 0, new ArrayList<>());
        }
        return null;
    }
}
