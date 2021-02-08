package fi.experis.chinooktunes.repository;

import fi.experis.chinooktunes.logger.CustomLogger;
import fi.experis.chinooktunes.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
public class SongRepository {

    private final CustomLogger logger;

    private final String URL = ConnectionHelper.CONNECTION_URL;
    private Connection conn = null;

    @Autowired
    public SongRepository(@Qualifier(value="logToConsole") CustomLogger logger) {
        this.logger = logger;
    }

    public ArrayList<Song> getRandomSongs(int numberOfSongs) {
        ArrayList<Song> randomSongs = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT t.Name AS TrackName, ar.Name AS ArtistName " +
                            "FROM artists ar " +
                            "         JOIN albums al ON ar.ArtistId = al.ArtistId " +
                            "         JOIN tracks t ON al.AlbumId = t.AlbumId " +
                            "ORDER BY random() " +
                            "LIMIT ?");
            preparedStatement.setInt(1, numberOfSongs);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                randomSongs.add(new Song(resultSet.getString("TrackName"),
                        resultSet.getString("ArtistName")));
            }

            logger.log("Requested items were retrieved successfully.");
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }
        return randomSongs;
    }
}
