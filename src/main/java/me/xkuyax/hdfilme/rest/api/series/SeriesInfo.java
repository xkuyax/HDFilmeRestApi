package me.xkuyax.hdfilme.rest.api.series;

import lombok.Getter;
import lombok.Setter;
import me.xkuyax.hdfilme.rest.api.film.FilmInfo;

@Getter
@Setter
public class SeriesInfo extends FilmInfo {

    private int maxEpisodes;
    private int currentEpisodes;

}
