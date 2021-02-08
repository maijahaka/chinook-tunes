package fi.experis.chinooktunes.controller;

import fi.experis.chinooktunes.model.Artist;
import fi.experis.chinooktunes.model.Genre;
import fi.experis.chinooktunes.model.Song;
import fi.experis.chinooktunes.repository.ArtistRepository;
import fi.experis.chinooktunes.repository.SongRepository;
import fi.experis.chinooktunes.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class DefaultController {

    private ArtistRepository artistRepository;
    private SongRepository songRepository;
    private GenreRepository genreRepository;

    @Autowired
    public DefaultController(ArtistRepository artistRepository,
                             SongRepository songRepository,
                             GenreRepository genreRepository) {
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
        this.genreRepository = genreRepository;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        ArrayList<Artist> randomArtists = artistRepository.getRandomArtists(5);
        model.addAttribute("artists", randomArtists);

        ArrayList<Song> randomSongs = songRepository.getRandomSongs(5);
        model.addAttribute("songs", randomSongs);

        ArrayList<Genre> randomGenres = genreRepository.getRandomGenres(5);
        model.addAttribute("genres", randomGenres);

        return "index";
    }

    @GetMapping("*")
    public ResponseEntity pageNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404 Page not found");
    }
}
