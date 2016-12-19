package me.xkuyax.hdfilme.rest.api.stream;

import lombok.Data;
import me.xkuyax.hdfilme.rest.api.QualityLevel;

@Data
public class VideoStreamLink {

    private String file;
    private String type;
    private String label;
    private QualityLevel qualityLevel;

}
