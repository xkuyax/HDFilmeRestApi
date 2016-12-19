package me.xkuyax.hdfilme.rest.api.download;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.xkuyax.hdfilme.rest.api.QualityLevel;

@Data
@AllArgsConstructor
public class VideoDownloadLink {

    private String fileName;
    private String url;
    private QualityLevel qualityLevel;

}