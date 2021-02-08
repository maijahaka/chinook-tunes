package fi.experis.chinooktunes.repository;

import fi.experis.chinooktunes.logger.CustomLogger;
import fi.experis.chinooktunes.model.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
public class ArtistRepository {

    private final CustomLogger logger;

    private final String URL = ConnectionHelper.CONNECTION_URL;
    private Connection conn = null;

    @Autowired
    public ArtistRepository(@Qualifier(value="logToConsole") CustomLogger logger) {
        this.logger = logger;
    }

    public ArrayList<Artist> getRandomArtists(int numberOfArtists) {
        ArrayList<Artist> randomArtists = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT Name FROM artists ORDER BY random() LIMIT ?");
            preparedStatement.setInt(1, numberOfArtists);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                randomArtists.add(new Artist(resultSet.getString("Name")));
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
        return randomArtists;
    }
}
