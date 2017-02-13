package me.xkuyax.hdfilme.rest.api.stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.xkuyax.hdfilme.rest.api.QualityLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoStreamLink {

    private String file;
    private String type;
    private String label;
    private QualityLevel qualityLevel;

}
