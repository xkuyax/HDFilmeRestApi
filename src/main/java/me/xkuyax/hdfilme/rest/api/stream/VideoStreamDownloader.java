package me.xkuyax.hdfilme.rest.api.stream;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.xkuyax.hdfilme.rest.api.FileUtils;
import me.xkuyax.hdfilme.rest.api.QualityLevel;
import me.xkuyax.hdfilme.rest.api.downloadapi.CacheDownloadHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class VideoStreamDownloader {

    private static final Type LINK_TYPE = new TypeToken<List<VideoStreamLink>>() {}.getType();
    private static final Gson gson = new Gson();
    private final CacheDownloadHandler downloadHandler;
    private final String url;
    private final boolean cache;

    public List<VideoStreamLink> getLinks() throws IOException {
        String html = cache ? getHtmlOffline() : getHtmlOnline();
        return Arrays.stream(html.split("\n")).map(line -> {
            if (line.contains("'sources' :")) {
                String json = line.substring(line.indexOf("["), line.lastIndexOf(","));
                JsonParser jsonParser = new JsonParser();
                jsonParser.parse(json);
                List<VideoStreamLink> links = gson.fromJson(json, LINK_TYPE);
                links.forEach(videoStreamLink -> {
                    Arrays.stream(QualityLevel.values()).filter(qualityLevel -> videoStreamLink.getLabel().contains(qualityLevel.getIdentifier())).findFirst().ifPresent(videoStreamLink::setQualityLevel);
                    videoStreamLink.setLabel(videoStreamLink.getLabel().substring(0, videoStreamLink.getLabel().length() - 1));
                });
                return links;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(new ArrayList<>());
    }

    private String getHtmlOffline() throws IOException {
        return downloadHandler.handleDownloadAsString(url, "film/" + FileUtils.removeInvalidFileNameChars(url));
    }

    private String getHtmlOnline() throws IOException {
        try (CloseableHttpResponse httpResponse = downloadHandler.getHttpClient().execute(new HttpGet(url))) {
            return EntityUtils.toString(httpResponse.getEntity());
        }
    }
}