package fi.experis.chinooktunes.repository;

import fi.experis.chinooktunes.logger.CustomLogger;
import fi.experis.chinooktunes.model.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
public class GenreRepository {

    private final CustomLogger logger;

    private final String URL = ConnectionHelper.CONNECTION_URL;
    private Connection conn = null;

    @Autowired
    public GenreRepository(@Qualifier(value="logToConsole") CustomLogger logger) {
        this.logger = logger;
    }

    public ArrayList<Genre> getRandomGenres(int numberOfGenres) {
        ArrayList<Genre> randomGenres = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT Name FROM genres ORDER BY random() LIMIT ?");
            preparedStatement.setInt(1, numberOfGenres);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                randomGenres.add(new Genre(resultSet.getString("Name")));
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
        return randomGenres;
    }
}
