package me.xkuyax.hdfilme.rest.api.series;

import lombok.Getter;
import lombok.Setter;
import me.xkuyax.hdfilme.rest.api.FilmInfo;

@Getter
@Setter
public class SeriesInfo extends FilmInfo {

    private int maxEpisodes;

}
