package me.xkuyax.hdfilme.rest.api.stream;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.xkuyax.hdfilme.rest.api.FileUtils;
import me.xkuyax.hdfilme.rest.api.QualityLevel;
import me.xkuyax.hdfilme.rest.api.downloadapi.CacheDownloadHandler;

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

    public List<VideoStreamLink> getLinks() throws IOException {
        String html = downloadHandler.handleDownloadAsString(url, FileUtils.removeInvalidFileNameChars(url));
        return Arrays.stream(html.split("\n")).map(line -> {
            if (line.contains("'sources' :")) {
                String json = line.substring(line.indexOf("["), line.lastIndexOf(","));
                JsonParser jsonParser = new JsonParser();
                jsonParser.parse(json);
                System.out.println(line);
                System.out.println(json);
                List<VideoStreamLink> links = gson.fromJson(json, LINK_TYPE);
                links.forEach(videoStreamLink -> Arrays.stream(QualityLevel.values()).filter(qualityLevel -> videoStreamLink.getLabel().contains(qualityLevel.getIdentifier())).findFirst().ifPresent(videoStreamLink::setQualityLevel));
                return links;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(new ArrayList<>());
    }
}
