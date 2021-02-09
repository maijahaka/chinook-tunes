package fi.experis.chinooktunes.controller;

import fi.experis.chinooktunes.model.SongWithAlbumAndGenre;
import fi.experis.chinooktunes.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class SearchController {

    private SongRepository songRepository;

    @Autowired
    public SearchController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @GetMapping("/search")
    public String showSearchResults(@RequestParam("searchTerm") String searchTerm, Model model) {

        // empty search criteria are not allowed
        if (!searchTerm.isBlank()) {
            ArrayList<SongWithAlbumAndGenre> searchResults =
                    songRepository.searchForSongsByName(searchTerm);

            model.addAttribute("searchResults", searchResults);
        } else {
            model.addAttribute("errorMessage", "Please specify a search criterion.");
        }

        model.addAttribute("searchTerm", searchTerm);

        return "search-results";
    }
}
