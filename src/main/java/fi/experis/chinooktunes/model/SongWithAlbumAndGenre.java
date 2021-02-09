package fi.experis.chinooktunes.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SongWithAlbumAndGenre extends Song {

    private String album;
    private String genre;

    public SongWithAlbumAndGenre(String name, String artist, String album, String genre) {
        super(name, artist);
        this.album = album;
        this.genre = genre;
    }
}
