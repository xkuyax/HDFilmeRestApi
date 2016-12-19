package me.xkuyax.hdfilme.rest.api.download;

import lombok.Data;
import me.xkuyax.hdfilme.rest.api.QualityLevel;
import me.xkuyax.hdfilme.rest.api.downloadapi.CacheDownloadHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class VideoDownloadLinkParser {

    private final CacheDownloadHandler downloadHandler;
    private final String link;
    private final String file;

    public List<VideoDownloadLink> parse() throws IOException {
        String html = downloadHandler.handleDownloadAsString(link, file);
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".row a");
        List<VideoDownloadLink> videoDownloadLinks = new ArrayList<>();
        for (Element element : elements) {
            String url = element.attr("href");
            if (url.contains("googlevideo.com")) {
                QualityLevel quality = Arrays.stream(QualityLevel.values()).filter(qualityLevel -> url.contains(qualityLevel.getIdentifier())).findFirst().orElse(QualityLevel.BULLSHIT);
                String fileName = url.split("title=")[1];
                videoDownloadLinks.add(new VideoDownloadLink(fileName, url, quality));
            }
        }
        return videoDownloadLinks;
    }
}
